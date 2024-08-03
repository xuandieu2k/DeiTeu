package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.FirebaseHandler;
import com.example.deiteu.model.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoginGoogle extends AppCompatActivity {
    private static final int RC_SIGN_IN = 101;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private ProgressDialog mloadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mloadingBar = new ProgressDialog(this);
        mloadingBar.setMessage("Đang kết nối Google với DeiTeu...");
        mloadingBar.show();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //
        mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app");
        mUserRef = mDatabase.getReference("Users");
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        Intent signInGGIt = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInGGIt,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount ac = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(ac.getIdToken());
            } catch (ApiException e) {
                mloadingBar.dismiss();
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            mloadingBar.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateInfor(user);
//                            if(user.getMetadata().getLastSignInTimestamp() == 0)
//                            {
//                                Intent it = new Intent(LoginGoogle.this,HelloUser.class);
//                                startActivity(it);
//                            }else{
//                                updateInfor(user);
//                            }
                            updateInfor(user);
                        }else{
                            Toast.makeText(LoginGoogle.this, ""+task.getException(), Toast.LENGTH_SHORT).show();
                            Log.e("Loi", String.valueOf(task.getException()));
                            mloadingBar.dismiss();
                            finish();
                        }
                    }
                });
    }

    private void updateInfor(FirebaseUser user) {
        String userId = user.getUid();
        String email = user.getEmail();
        String name = user.getDisplayName();
        String photoUrl = user.getPhotoUrl().toString();
        if(user != null)
        {
            long creationTimestamp = user.getMetadata().getCreationTimestamp() / 1000;
            LocalDateTime dateTime = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                dateTime = LocalDateTime.ofEpochSecond(creationTimestamp, 0, java.time.ZoneOffset.UTC);
            }
            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            }
            String formattedDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formattedDate = dateTime.format(formatter);

            }
            String finalFormattedDate = formattedDate;
            mUserRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        Users newUser = new Users();
                        newUser.setId(userId);
                        newUser.setFullname(name);
                        newUser.setAvatar(photoUrl);
                        newUser.setEmail(email);
                        newUser.setJoindate(finalFormattedDate);
                        mUserRef.child(userId).setValue(newUser);
                    }else{
                        mUserRef.child(user.getUid()).child("joindate").setValue(finalFormattedDate);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        firebaseHandler.Login(FirebaseAuth.getInstance().getUid());
        Intent itent = new Intent(LoginGoogle.this, Home2.class);
        itent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(itent);
    }
    private FirebaseHandler firebaseHandler = new FirebaseHandler();

//    private void updateUI(FirebaseUser user) {
//        String userId = user.getUid();
//        String email = user.getEmail();
//        String name = user.getDisplayName();
//        String photoUrl = user.getPhotoUrl().toString();
//        Log.d("User Id", ": Id:" + userId);
//        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Nếu người dùng đã tồn tại, cập nhật thông tin của họ
//                long userCount = dataSnapshot.getChildrenCount();
//                if (dataSnapshot.exists()) {
//                    mUserRef.child(userId).child("id").setValue(userId);
//                    mUserRef.child(userId).child("fullname").setValue(name);
//                    mUserRef.child(userId).child("email").setValue(email);
////                    mUserRef.child(userId).child("avatar").setValue(photoUrl);
//                }
//                // Nếu người dùng chưa tồn tại, thêm mới vào Realtime Database
//                else {
//                    Users newUser = new Users();
//                    newUser.setId(userId);
//                    newUser.setFullname(name);
//                    newUser.setAvatar(photoUrl);
//                    newUser.setEmail(email);
//                    newUser.setBirthday("");
//                    newUser.setGender("");
//                    newUser.setPassword("");
//                    mUserRef.child(userId).setValue(newUser);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Xử lý khi có lỗi xảy ra
//                Log.e("User count", "Failed to read user count.", databaseError.toException());
//            }
//        });
//        Intent itent = new Intent(LoginGoogle.this, Home2.class);
//        itent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(itent);
//    }
}
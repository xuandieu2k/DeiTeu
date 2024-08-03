package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.FirebaseHandler;
import com.example.deiteu.model.Users;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class LoginFacebook extends AppCompatActivity {

    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);
        //
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //
        mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app");
        mUserRef = mDatabase.getReference("Users");
        //
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginFacebook.this, "Đăng nhập thành công.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateInfor(user);
                        } else {
                            Toast.makeText(LoginFacebook.this, "Đã có lỗi xảy ra: "+task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            updateInfor(null);
                        }
                    }
                });
    }
    private void updateInfor(FirebaseUser user) {
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
                        mUserRef.child(user.getUid()).child("id").setValue(user.getUid());
                        mUserRef.child(user.getUid()).child("fullname").setValue(user.getDisplayName());
                        mUserRef.child(user.getUid()).child("email").setValue(user.getEmail());
                        mUserRef.child(user.getUid()).child("avatar").setValue(user.getPhotoUrl());
                        mUserRef.child(user.getUid()).child("joindate").setValue(finalFormattedDate);
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
        Intent intent = new Intent(LoginFacebook.this, Home2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    private FirebaseHandler firebaseHandler = new FirebaseHandler();

//    private void updateUI(FirebaseUser user) {
//
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
//                if (!dataSnapshot.exists()){
//                    Users newUser = new Users();
//                    newUser.setId(userId);
//                    newUser.setFullname(name);
//                    newUser.setAvatar(photoUrl);
//                    newUser.setEmail(email);
////                    newUser.setBirthday("");
////                    newUser.setGender("");
////                    newUser.setPassword("");
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
//        Intent intent = new Intent(LoginFacebook.this, Home2.class);
////        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
}
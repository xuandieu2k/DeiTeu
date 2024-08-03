package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.FirebaseHandler;
import com.example.deiteu.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import io.woong.shapedimageview.CircleImageView;

public class Login extends AppCompatActivity {
    AppCompatButton btnlogin;
    CircleImageView btnfacebook,btngoogle;
    TextView tvSignup,tvforgetpass;

    EditText edEmail,edPass;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private ProgressDialog mloadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // map layout
        tvSignup = findViewById(R.id.tvsignup);
        btnlogin = findViewById(R.id.btn_login);
        btngoogle = findViewById(R.id.btngoogle);
        btnfacebook = findViewById(R.id.btnfacebook);
        tvforgetpass = findViewById(R.id.tvForgetpass);
        edEmail = findViewById(R.id.edtEmail);
        edPass = findViewById(R.id.edtPassword);
        edPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (edPass.getText().length() > 0) {
                        edPass.setHint("");
                    }
                }
            }
        });


        // map firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app");
        mUserRef = mDatabase.getReference("Users");
        mloadingBar = new ProgressDialog(Login.this);

        // click signup
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup.class));
            }
        });
        // click forget password
        tvforgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, ForgetPassword.class));
            }
        });
        // click login gg
        btngoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, LoginGoogle.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        // click login facebook
        btnfacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, LoginFacebook.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        // click login
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidInput();
            }
        });
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    private void checkValidInput() {
        String email = edEmail.getText().toString();
        String pass = edPass.getText().toString();
        // Kiểm tra email
        if(email.isEmpty())
        {
            edEmail.setError("Email không được bỏ trống.");
            edEmail.requestFocus();
        }else{
            if(isValidEmail(email))
            {

            }else {
                edEmail.setError("Email không hợp lệ.");
                edEmail.requestFocus();
            }
        }
        // Kiểm tra mật khẩu
        if(pass.isEmpty())
        {
            edPass.setError("Mật khẩu không được bỏ trống.");
            edPass.requestFocus();
        }else{
        }
        //
        if(isValidEmail(email) && !pass.isEmpty())
        {
            mloadingBar.setMessage("Vui lòng chờ, Hệ thống đang xác thực thông tin của bạn...");
            mloadingBar.setTitle("Xác thực");
            mloadingBar.setCanceledOnTouchOutside(false);
            mloadingBar.show();
            //
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> signInMethods = task.getResult().getSignInMethods();
                            if (signInMethods != null && signInMethods.size() > 0) {
                                // Email tồn tại
                                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            mloadingBar.dismiss();
                                            updateInfor(user);
                                            sendUserToNextActivity();
                                            Toast.makeText(Login.this,"Đăng nhập thành công!",Toast.LENGTH_SHORT).show();
                                        }else{
                                            mloadingBar.dismiss();
//                                            Toast.makeText(Login.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                            Toast.makeText(Login.this,"Email hoặc mật khẩu không chính xác.",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                // Email không tồn tại
                                mloadingBar.dismiss();
                                Toast.makeText(Login.this,"Email không tồn tại.\nVui lòng đăng ký tài khoản bằng email để tận hưởng dịch vụ của DeiTeu.",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Kiểm tra email thất bại
                        }
                    });
            //

        }
    }

    private void updateInfor(FirebaseUser user) {
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

    private void sendUserToNextActivity() {
        //
        firebaseHandler.Login(FirebaseAuth.getInstance().getUid());
        Intent it  = new Intent(Login.this, Home2.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }

    public boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
    private FirebaseHandler firebaseHandler = new FirebaseHandler();
}
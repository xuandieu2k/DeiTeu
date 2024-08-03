package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

public class ForgetPassword extends AppCompatActivity {
    TextView tvBacktoLogin;
    AppCompatButton btnGetPassword;
    EditText edtEmail;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        tvBacktoLogin =findViewById(R.id.tvBacklogin);
        btnGetPassword = findViewById(R.id.btn_forget);
        edtEmail = findViewById(R.id.edtEmail);
        mAuth = FirebaseAuth.getInstance();
        tvBacktoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgetPassword.this, Login.class));
                finish();
            }
        });
        btnGetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInvalid();
            }
        });
    }

    public boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
    private void checkInvalid() {
        String email = edtEmail.getText().toString();
        // Kiểm tra email
        if (email.isEmpty()) {
            edtEmail.setError("Email không được bỏ trống.");
            edtEmail.requestFocus();
        } else {
            if (isValidEmail(email)) {
                forgetPassWord(email);
            } else {
                edtEmail.setError("Email không hợp lệ.");
                edtEmail.requestFocus();
            }
        }
    }

    private void forgetPassWord(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPassword.this);
                    builder.setTitle("Cấp lại mật khẩu");
                    builder.setMessage("Chúng tôi đã gửi cho bạn một email để lấy lại mật khẩu.\nVui lòng làm theo hướng dẫn để tiếp tục trải nghiệm");
                    // Tạo nút "OK" và xử lý sự kiện khi người dùng nhấn vào nó
                    builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Xử lý khi người dùng nhấn OK
                            // Tạo Toast với thời lượng hiển thị là 5 giây
                            Toast toast = Toast.makeText(ForgetPassword.this, "Vui lòng kiểm tra Email để cập nhật mật khẩu mới.", Toast.LENGTH_LONG);
                            // Hiển thị Toast
                            toast.show();
                            // Tự động ẩn Toast sau 5 giây
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    toast.cancel();
                                }
                            }, 10000);
                            startActivity(new Intent(ForgetPassword.this,Login.class));
                            finish();
                        }
                    });
                    // Hiển thị AlertDialog
                    builder.show();

                }else
                {
                    Toast.makeText(ForgetPassword.this, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
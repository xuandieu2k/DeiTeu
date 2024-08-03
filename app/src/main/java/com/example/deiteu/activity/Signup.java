package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    TextView tvlogin;
    AppCompatButton btnSignup;
    EditText edFullnam,edEmail,edPass,edConfirm;

    private FirebaseAuth mAuth;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserRef;
    private ProgressDialog mloadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        tvlogin = findViewById(R.id.tvsignup);
        btnSignup = findViewById(R.id.btn_signup);
        edFullnam = findViewById(R.id.edtFullname);
        edEmail = findViewById(R.id.edtEmail);
        edPass = findViewById(R.id.edtPassword);
        edConfirm = findViewById(R.id.edtConfirmPassword);
        mAuth = FirebaseAuth.getInstance();
        //
        mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app");
        mloadingBar = new ProgressDialog(Signup.this);
        tvlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Signup.this, Login.class));
                finish();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInvalid();
            }
        });
    }

    private void checkInvalid() {
        String fullname = edFullnam.getText().toString();
        String email = edEmail.getText().toString();
        String pass = edPass.getText().toString();
        String confirm = edConfirm.getText().toString();
        LocalDateTime currentDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }
        String formattedDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDate = currentDateTime.format(formatter);
        }
        String joindate = formattedDate;
        // Kiểm tra họ và tên
        if (fullname.isEmpty()) {
            edFullnam.setError("Họ và tên không được bỏ trống.");
            edFullnam.requestFocus();
        } else {
            if (isValidFullName(fullname)) {

            } else {
                edFullnam.setError("Họ và tên không hợp lệ.");
                edFullnam.requestFocus();
            }
        }
        // Kiểm tra email
        if (email.isEmpty()) {
            edEmail.setError("Email không được bỏ trống.");
            edEmail.requestFocus();
        } else {
            if (isValidEmail(email)) {

            } else {
                edEmail.setError("Email không hợp lệ.");
                edEmail.requestFocus();
            }
        }
        // Kiểm tra mật khẩu
        if (pass.isEmpty()) {
            edPass.setError("Mật khẩu không được bỏ trống.");
            edPass.requestFocus();
        } else {
            if (isValidPassword(pass)) {

            } else {
                edPass.setError("Mật khẩu không hợp lệ.\nMật khẩu phải có ít nhất 8 ký tự(một chữ hoa, một chữ thường, một số và một ký tự đặc biệt)");
                edPass.requestFocus();
            }
        }
        // Kiểm tra xác nhận mật khẩu
        if (confirm.isEmpty()) {
            edConfirm.setError("Mật khẩu không được bỏ trống.");
            edConfirm.requestFocus();
        } else {
            if (isValidPassword(confirm)) {
                if(pass.equals(confirm)) {
                }else {
                    edConfirm.setError("Mật khẩu không trùng khớp.");
                    edConfirm.requestFocus();
                }
            } else {
                edConfirm.setError("Mật khẩu không hợp lệ.\nMật khẩu phải có ít nhất 8 ký tự(một chữ hoa, một chữ thường, một số và một ký tự đặc biệt)");
                edConfirm.requestFocus();
            }
        }
        //
        if (isValidPassword(pass) && pass.equals(confirm) && isValidEmail(email) && isValidPassword(confirm) && isValidFullName(fullname)) {
            mloadingBar.setMessage("Vui lòng chờ, Hệ thống đang kiểm tra thông tin của bạn...");
            mloadingBar.setTitle("Xác thực");
            mloadingBar.setCanceledOnTouchOutside(false);
            mloadingBar.show();
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> signInMethods = task.getResult().getSignInMethods();
                            if (signInMethods != null && signInMethods.size() > 0) {
                                // Email đã được đăng ký trên Firebase
                                // Hiển thị thông báo cho người dùng rằng email này đã tồn tại
                                // và yêu cầu họ sử dụng một địa chỉ email khác để đăng ký tài khoản
                                mloadingBar.dismiss();
                                Toast.makeText(Signup.this,"Email đã được đăng ký.\nVui lòng chọn email khác hoặc quay lại đăng nhập.",Toast.LENGTH_SHORT).show();
                            } else {
                                // Email chưa được đăng ký trên Firebase
                                // Tiến hành đăng ký tài khoản mới
                                mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            mloadingBar.dismiss();
                                            // Lấy UID của người dùng mới được tạo
                                            String uid = mAuth.getCurrentUser().getUid();
                                            // Tạo một tham chiếu tới địa chỉ trên database để lưu thông tin tài khoản người dùng
                                            mUserRef = mDatabase.getReference("Users").child(uid);

                                            // Lưu thông tin tài khoản người dùng vào database
                                            Users user = new Users(uid,fullname,email,joindate);
                                            // Tạo đối tượng User với thông tin cần lưu
                                            mUserRef.setValue(user);
                                            sendUserToNextActivity();
                                            Toast.makeText(Signup.this,"Đăng ký thành công.",Toast.LENGTH_SHORT).show();
                                        }else{
                                            mloadingBar.dismiss();
                                            Toast.makeText(Signup.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            mloadingBar.dismiss();
                            //
                            Toast.makeText(Signup.this,"Đã có lỗi xảy ra.\nVui lòng thử lại sau.",Toast.LENGTH_SHORT).show();
                        }
                    });
        }else {
//            Toast.makeText(Signup.this,"Thông tin không hợp lệ.\nVui lòng kiểm tra lại toàn bộ thông tin.",Toast.LENGTH_SHORT).show();
        }
    }
    public static boolean isValidFullName(String fullName) {
        // Kiểm tra nếu fullname rỗng hoặc null
        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        // Tách fullname thành các phần riêng lẻ bằng khoảng trắng
        String[] nameParts = fullName.trim().split("\\s+");

        // Kiểm tra số lượng phần tách được
        if (nameParts.length < 2) {
            return false;
        }

        // Kiểm tra mỗi phần tách được
//        for (String namePart : nameParts) {
//            if (!namePart.matches("[a-zA-ZđĐàáạảãăắằẳẵặâấầẩẫậèéẹẻẽêếềểễệìíịỉĩòóọỏõôốồổỗộơớờởỡợùúụủũưứừửữựỳýỵỷỹ]+")) {
//                return false;
//            }
//        }

        return true;
    }

    public boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );
    public static boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    private void sendUserToNextActivity() {
//        firebaseHandler.changeStatus(FirebaseAuth.getInstance().getUid());
        Intent it  = new Intent(Signup.this, Home2.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
    }
    private FirebaseHandler firebaseHandler = new FirebaseHandler();
}
package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class HelloUser extends AppCompatActivity {
    private static final int REQUEST_CODE_IMAGE = 101;
    private ImageView img_avatar;
    private EditText edt_birthday;
    private TextView tv_hello;
    private String changeGender = "";
    private String changeBirthday = "";
    private AppCompatButton btn_save;
    private StorageReference storageref = FirebaseStorage.getInstance().getReference().child("UserImage");
    ;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(String.valueOf(FirebaseAuth.getInstance().getUid()));
    private Uri imagesUri;
    private boolean isImageAdded = false;
    RelativeLayout female_layout, male_layout, other_layout;
    RadioButton radio_female, radio_male, radio_other;
    Users users = new Users();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_user);
        img_avatar = findViewById(R.id.img_avatar);
        edt_birthday = findViewById(R.id.edt_birthday);
        tv_hello = findViewById(R.id.tv_hello);
        btn_save = findViewById(R.id.btn_save);
        female_layout = findViewById(R.id.female_layout);
        male_layout = findViewById(R.id.male_layout);
        other_layout = findViewById(R.id.other_layout);
        radio_female = findViewById(R.id.radio_female);
        radio_male = findViewById(R.id.radio_male);
        radio_other = findViewById(R.id.radio_twogender);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    users = snapshot.getValue(Users.class);
                    selectedGender(users.getAvatar(), radio_male, radio_female, radio_other, male_layout, female_layout, other_layout);
                    if (users.getAvatar() != null) {
                        Picasso.get().load(users.getAvatar()).into(img_avatar);
                    }
                    if (users.getFullname() != null) {
                        tv_hello.setText("Chào, " + users.getFullname());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        radio_male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    male_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_genderbgred));
                    radio_female.setChecked(false);
                    radio_other.setChecked(false);
                    radio_male.setChecked(true);
                    changeGender = "1";
                } else {
                    male_layout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                }
            }
        });

        radio_female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    female_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_genderbgred));
                    radio_male.setChecked(false);
                    radio_other.setChecked(false);
                    radio_female.setChecked(true);
                    changeGender = "2";
                } else {
                    female_layout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                }
            }
        });

        radio_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    other_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_genderbgred));
                    radio_male.setChecked(false);
                    radio_female.setChecked(false);
                    radio_other.setChecked(true);
                    changeGender = "0";
                } else {
                    other_layout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                }
            }
        });
        // 0 unknown 1 male 2 female

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        edt_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!changeAvatar() && !changeBirthday() && !changeGenderFunc())
                {
                    Toast.makeText(HelloUser.this, "Vui lòng nhập đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                }else{
                    if (changeAvatar()) {
                        uploadImagesAvatar();
                        mDatabase.child("gender").setValue(changeGender).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    checkgender = true;
                                } else {
                                    checkgender = false;
                                }
                            }
                        });
                        mDatabase.child("birthday").setValue(changeBirthday).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    checkbirthday = true;
                                } else {
                                    checkbirthday = false;
                                }
                            }
                        });

                        if(!checkbirthday && !checkgender && !checkuploadavatar)
                        {
                            Toast.makeText(HelloUser.this, "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(HelloUser.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(HelloUser.this, Home2.class);
                            startActivity(it);
                        }
                    } else {
                        mDatabase.child("gender").setValue(changeGender).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    checkgender = true;
                                    mDatabase.child("birthday").setValue(changeBirthday).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                checkbirthday = true;
                                            } else {
                                                checkbirthday = false;
                                            }
                                            if(!checkbirthday && !checkgender)
                                            {
                                                Toast.makeText(HelloUser.this, "Đã có lỗi xảy ra.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(HelloUser.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                                                Intent it = new Intent(HelloUser.this, Home2.class);
                                                startActivity(it);
                                            }
                                        }
                                    });
                                } else {
                                    checkgender = false;
                                }
                            }
                        });
                    } 
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void selectedGender(String gender, RadioButton male, RadioButton female, RadioButton other, RelativeLayout male_layout, RelativeLayout female_layout, RelativeLayout other_layout) {
        switch (gender) {
            case "0":
                other.setChecked(true);
                other_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                break;
            case "1":
                male.setChecked(true);
                male_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                break;
            case "2":
                female.setChecked(true);
                female_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                break;
            default:
        }
    }

    // Avatar
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && data != null) {
            imagesUri = data.getData();
            isImageAdded = true;
            img_avatar.setImageURI(imagesUri);
        }
    }

    public boolean changeAvatar() {
        if (users.getAvatar() != null) {
            if (isImageAdded) {
                return true;
            } else {
                return false;
            }
        } else {
            if (isImageAdded) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean changeGenderFunc() {
        if (users.getGender() != null && users.getGender() != "") {
            if (users.getGender() != changeGender && changeGender != "") {
                return true;
            } else {
                return false;
            }
        } else {
            if (users.getGender() != changeGender && changeGender != "") {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean changeBirthday() {
        if (users.getBirthday() != null && users.getGender() != "") {
            if (users.getBirthday() != changeBirthday && changeBirthday != "") {
                changeBirthday = changeBirthday.replaceAll("\\s+", "");
                return true;
            } else {
                return false;
            }
        } else {
            if (users.getBirthday() != changeBirthday && changeBirthday != "") {
                changeBirthday = changeBirthday.replaceAll("\\s+", "");
                return true;
            } else {
                return false;
            }
        }
    }

    // Datetime
    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        // Tạo đối tượng SimpleDateFormat để phân tích chuỗi datetime thành ngày, tháng và năm
        DateFormat dateFormat = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault());
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
            }
        };
        // Tạo DatePickerDialog với ngôn ngữ tiếng Việt
        Locale locale = new Locale("vi", "VN");
        Locale.setDefault(locale);
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                R.style.CustomDialogTheme,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                int year = datePicker.getYear();
                int month = datePicker.getMonth();
                int dayOfMonth = datePicker.getDayOfMonth();
                // Xử lý khi người dùng chọn "Ok" trên DatePickerDialog
                // Format ngày tháng sang chuỗi
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                String selectedDate = dateFormat.format(calendar.getTime());
                // Do something with selectedDate
                changeBirthday = selectedDate;
                edt_birthday.setText(selectedDate);
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn "Hủy" trên DatePickerDialog
            }
        });
//        datePickerDialog.setTitle("Chọn ngày");
    }

    private void uploadImagesAvatar() {
        final String key = mDatabase.push().getKey();
        storageref.child(key + ".jpg").putFile(imagesUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageref.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mDatabase.child("avatar").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                checkuploadavatar = true;
                            }
                        });
                    }
                });
            }
        });
    }

    private boolean checkuploadavatar = false;
    boolean checkgender, checkbirthday = false;
}
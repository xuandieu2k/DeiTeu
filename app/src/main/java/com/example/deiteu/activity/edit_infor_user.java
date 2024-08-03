package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.Love;
import com.example.deiteu.model.Users;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class edit_infor_user extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE = 101;
    private ImageButton btn_back, btn_save;

    private EditText edt_descriptmyself;

    private TextView tv_countchar;


    private AppCompatButton btn_fullname, btn_birthday, btn_gender;

    private LinearLayout layout_birthday, layout_fullname, layout_avatar, layout_background, layout_gender;
    private CircleImageView cirleAvatar;
    private ImageView imgbackground;
    // dialog background
    Uri imagesUri;
    private boolean isImageAdded = false;

    private boolean isChangeInfor = false;

    private boolean isChangeSuccessDes, isChangeSuccessBirthday, isChangeSuccessfullname, isChangeSusscessGender = false;

    private String originalText, originalfullname, originalbirthday, originalGender, changeGender = "";
    private boolean hasTextChanged = false;
    private ImageView imageView;
    private ProgressBar progressBar;
    private Button saveButton;
    private TextView tvpercent;
    private Dialog dialog, dialogavatar;

    private StorageReference storageref = FirebaseStorage.getInstance().getReference().child("UserImage");
    ;
    //
    private DatePickerDialog.OnDateSetListener dateSetListener;

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(String.valueOf(FirebaseAuth.getInstance().getUid()));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_infor_user);
        btn_back = findViewById(R.id.btnimg_back);
        btn_save = findViewById(R.id.btnimg_save);
        cirleAvatar = findViewById(R.id.img_avatar);
        imgbackground = findViewById(R.id.img_background);
        btn_birthday = findViewById(R.id.btn_birthday);
        btn_gender = findViewById(R.id.btn_gender);
        btn_fullname = findViewById(R.id.btn_fullname);
        tv_countchar = findViewById(R.id.tv_countchar);
        edt_descriptmyself = findViewById(R.id.edt_desyourself);
        layout_birthday = findViewById(R.id.layout_birthday);
        layout_fullname = findViewById(R.id.layout_fullname);
        layout_avatar = findViewById(R.id.layout_avatar);
        layout_background = findViewById(R.id.layout_background);
        layout_gender = findViewById(R.id.layout_gender);
        //
        checkgender();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    if (users.getFullname() != null && users.getFullname() != "") {
                        btn_fullname.setText(users.getFullname());
                        originalfullname = users.getFullname();
                    } else {
                        btn_fullname.setText("Unknown name");
                    }
                    if (users.getBirthday() != null && users.getBirthday() != "") {
                        btn_birthday.setText(users.getBirthday());
                        originalbirthday = users.getBirthday();
                    } else {
                        btn_birthday.setText("?? / ?? / ????");
                    }
                    if (users.getDescription() != null && users.getDescription() != "") {
                        originalText = users.getDescription();
                        edt_descriptmyself.setText(users.getDescription());
                    } else {
                        edt_descriptmyself.setText("");
                    }
                    // Sử dụng giá trị  ở đây
                    if (users.getBackground() != null) {
                        Picasso.get().load(users.getBackground()).into(imgbackground);
                    }
                    if (users.getAvatar() != null) {
                        Picasso.get().load(users.getAvatar()).into(cirleAvatar);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        edt_descriptmyself.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Đánh dấu nếu có sự thay đổi trong EditText
                if (!hasTextChanged) {
                    hasTextChanged = !s.toString().equals(originalText);
                }

                // Nếu đã có sự thay đổi và đang xảy ra thêm thay đổi, gán giá trị true cho isChangeInfor
                if (hasTextChanged) {
                    int charCount = countCharacters(s.toString());
                    tv_countchar.setText(charCount + " / 200");
                    if (edt_descriptmyself.getText().toString().trim().equals(originalText)) {
                        isChangeInfor = false;
                    } else {
                        isChangeInfor = true;
                    }
                }
            }


            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = simpleDateFormat.format(calendar.getTime());
                btn_birthday.setText(date);
                isChangeInfor = true;
            }
        };
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChangeInfor) {
                    //
                    final Dialog dialogCompat = new Dialog(edit_infor_user.this);
                    dialogCompat.setContentView(R.layout.dialog_confirmlist);
                    AppCompatButton btn_oke = dialogCompat.findViewById(R.id.btn_ok);
                    AppCompatButton btn_cancel = dialogCompat.findViewById(R.id.btn_cancel);
                    TextView tv = dialogCompat.findViewById(R.id.tv_title);
                    tv.setText("Chưa lưu.\n\nBạn vẫn muốn thoát chứ?");
                    dialogCompat.show();
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogCompat.dismiss();
                        }
                    });
                    btn_oke.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogCompat.dismiss();
                            finish();
                        }
                    });

                } else {
                    finish();
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChangeInfor) {
                    String fullname, gender, birthday, des = "";
                    if (!btn_fullname.getText().toString().equals(originalfullname)) {
                        fullname = btn_fullname.getText().toString();
                    } else {
                        fullname = originalfullname;
                    }
                    if (!changeGender.equals(originalGender)) {
                        gender = changeGender;
                    } else {
                        gender = originalGender;
                    }
                    if (!btn_birthday.getText().toString().equals(originalbirthday)) {
                        birthday = btn_birthday.getText().toString();
                    } else {
                        birthday = originalbirthday;
                    }
                    if (!edt_descriptmyself.getText().toString().equals(originalText)) {
                        des = edt_descriptmyself.getText().toString();
                    } else {
                        des = originalText;
                    }
                    UpdateInfor(fullname, gender, birthday, des);
                    isChangeInfor = false;
                    Toast.makeText(edit_infor_user.this, "Đã cập nhật lại hồ sơ cá nhân.", Toast.LENGTH_SHORT).show();
                } else {
                }
            }
        });
        setOnClickListenerslayout_birthday(layout_birthday);
        setOnClickListenerslayout_background(layout_background);
        setOnClickListenerslayout_avatar(layout_avatar);
        setOnClickListenerslayout_fullname(layout_fullname);
        setOnClickListenerslayout_gender(layout_gender);
    }

    public void UpdateInfor(String fullname, String gender, String birthday, String descript) {
        if (fullname != null && fullname != "") {
            mDatabase.child("fullname").setValue(fullname);
        }
        if (gender != null && gender != "") {
            mDatabase.child("gender").setValue(gender);
        }
        if (birthday != null && birthday != "" && birthday != "?? / ?? / ????") {
            mDatabase.child("birthday").setValue(birthday);
        }
        if (descript != null && descript != "") {
            mDatabase.child("description").setValue(descript);
        }
    }

    boolean check = false;

    private void checkgender() {
        mDatabase.child("gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String gender = snapshot.getValue().toString();
                    originalGender = gender;
                    changeGender = gender;
                    switch (gender) {
                        case "0":
                            btn_gender.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_twogender2), null, getDrawable(R.drawable.icon_next), null);
                            btn_gender.setText("LGBT");
                            break;
                        case "1":
                            btn_gender.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_male), null, getDrawable(R.drawable.icon_next), null);
                            btn_gender.setText("Nam");
                            break;
                        case "2":
                            btn_gender.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_female), null, getDrawable(R.drawable.icon_next), null);
                            btn_gender.setText("Nữ");
                            break;
                        default:
                    }
                } else {
                    originalGender = "";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void setOnClickListenerslayout_birthday(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListenerslayout_birthday(childView);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                    showDateDialog();
                }
            });
        }
    }

    public void setOnClickListenerslayout_gender(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListenerslayout_gender(childView);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                    showDialogGender();
                }
            });
        }
    }

    private void showDialogGender() {
        final Dialog dialog = new Dialog(edit_infor_user.this);
        dialog.setContentView(R.layout.edit_gender_user);
        final RelativeLayout female_layout = dialog.findViewById(R.id.female_layout);
        final RelativeLayout male_layout = dialog.findViewById(R.id.male_layout);
        final RelativeLayout other_layout = dialog.findViewById(R.id.other_layout);
        final RadioButton radio_female = dialog.findViewById(R.id.radio_female);
        final RadioButton radio_male = dialog.findViewById(R.id.radio_male);
        final RadioButton radio_other = dialog.findViewById(R.id.radio_twogender);
//        radio_female.setChecked(false);
//        radio_male.setChecked(false);
//        radio_other.setChecked(false);
        radio_male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    male_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                    radio_female.setChecked(false);
                    radio_other.setChecked(false);
                    changeGender = "1";
                    updateGendertemp(changeGender);
                    if (!changeGender.equals(originalGender)) {
                        isChangeInfor = true;
                    } else {
                        isChangeInfor = false;
                    }
                } else {
                    male_layout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                }
            }
        });

        radio_female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    female_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                    radio_male.setChecked(false);
                    radio_other.setChecked(false);
                    changeGender = "2";
                    updateGendertemp(changeGender);
                    if (!changeGender.equals(originalGender)) {
                        isChangeInfor = true;
                    } else {
                        isChangeInfor = false;
                    }
                } else {
                    female_layout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                }
            }
        });

        radio_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    other_layout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));

                    radio_male.setChecked(false);
                    radio_female.setChecked(false);
                    changeGender = "0";
                    updateGendertemp(changeGender);
                    if (!changeGender.equals(originalGender)) {
                        isChangeInfor = true;
                    } else {
                        isChangeInfor = false;
                    }
                } else {
                    other_layout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                }
            }
        });
        // 0 unknown 1 male 2 female
        mDatabase.child("gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String gender = snapshot.getValue().toString();
                    selectedGender(gender, radio_male, radio_female, radio_other, male_layout, female_layout, other_layout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dialog.show();
    }

    private void updateGendertemp(String changeGender) {
        switch (changeGender) {
            case "0":
                btn_gender.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_twogender2), null, getDrawable(R.drawable.icon_next), null);
                btn_gender.setText("LGBT");
                break;
            case "1":
                btn_gender.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_male), null, getDrawable(R.drawable.icon_next), null);
                btn_gender.setText("Nam");
                break;
            case "2":
                btn_gender.setCompoundDrawablesRelativeWithIntrinsicBounds(getDrawable(R.drawable.icon_female), null, getDrawable(R.drawable.icon_next), null);
                btn_gender.setText("Nữ");
                break;
            default:
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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

    public void setOnClickListeners_female_layout(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListeners_female_layout(childView);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                }
            });
        }
    }

    public void setOnClickListenerslayout_fullname(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListenerslayout_fullname(childView);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                    showdialogfullname();
                }
            });
        }
    }

    private void showdialogfullname() {
        final Dialog dialog = new Dialog(edit_infor_user.this);
        dialog.setContentView(R.layout.dialog_editfullname);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        AppCompatButton btn_success = dialog.findViewById(R.id.btn_success);
        EditText edt_fullname = dialog.findViewById(R.id.edt_editfullname);
        edt_fullname.setText(btn_fullname.getText().toString());
        dialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = edt_fullname.getText().toString();
                if (fullname.equals("")) {
                    edt_fullname.setError("Vui lòng nhập họ tên của bạn trước khi cập nhật.");
                } else {
                    if (!fullname.equals(originalfullname)) {
                        isChangeInfor = true;
                    } else {
                        isChangeInfor = false;
                    }
                    btn_fullname.setText(fullname);
                    dialog.dismiss();
                }
            }
        });
    }

    public void setOnClickListenerslayout_background(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListenerslayout_background(childView);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                    changeBackground();
                    isImageAdded = false;
                }
            });
        }
    }

    public void setOnClickListenerslayout_avatar(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListenerslayout_avatar(childView);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                    changeAvatar();
                    isImageAdded = false;
                }
            });
        }
    }

    private int countCharacters(String text) {
        return text.length();
    }

    private void changeBackground() {
        dialog = new Dialog(edit_infor_user.this);
        dialog.setContentView(R.layout.dialog_changepicture);

        imageView = dialog.findViewById(R.id.imageView);
        progressBar = dialog.findViewById(R.id.progressBar);
        saveButton = dialog.findViewById(R.id.saveButton);
        tvpercent = dialog.findViewById(R.id.tvpercent);
        //
        mDatabase.child("background").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String background = snapshot.getValue(String.class);
                // Sử dụng giá trị  ở đây
                if (background != null) {
                    Picasso.get().load(background).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        dialog.show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện lưu thay đổi
//                String imageName = getImageName(imagesUri);
//                tvpercent.setText(imageName);
                if (isImageAdded) {
                    uploadImages();
                }
            }
        });

    }

    private void changeAvatar() {
        dialogavatar = new Dialog(edit_infor_user.this);
        dialogavatar.setContentView(R.layout.dialog_changeavatar);

        imageView = new CircleImageView(edit_infor_user.this);
        imageView = dialogavatar.findViewById(R.id.imageView);
        progressBar = dialogavatar.findViewById(R.id.progressBar);
        saveButton = dialogavatar.findViewById(R.id.saveButton);
        tvpercent = dialogavatar.findViewById(R.id.tvpercent);
        //
        mDatabase.child("avatar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String avatar = snapshot.getValue(String.class);
                // Sử dụng giá trị  ở đây
                if (avatar != null) {
                    Picasso.get().load(avatar).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        dialogavatar.show();
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện lưu thay đổi
//                String imageName = getImageName(imagesUri);
//                tvpercent.setText(imageName);
                if (isImageAdded) {
                    uploadImagesAvatar();
                }
            }
        });

    }

    private void uploadImages() {
        tvpercent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        final String key = mDatabase.push().getKey();
        storageref.child(key + ".jpg").putFile(imagesUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageref.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mDatabase.child("background").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(edit_infor_user.this, "Cập nhật hình nền thành công.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = snapshot.getBytesTransferred() * 100 / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                tvpercent.setText(progress + " %");
            }
        });
    }

    private void uploadImagesAvatar() {
        tvpercent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

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
                                Toast.makeText(edit_infor_user.this, "Cập nhật hình đại diện thành công.", Toast.LENGTH_SHORT).show();
                                dialogavatar.dismiss();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = snapshot.getBytesTransferred() * 100 / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);
                tvpercent.setText(progress + " %");
            }
        });
    }

    // Phương thức để lấy tên của hình ảnh từ Uri
    @SuppressLint("Range")
    private String getImageName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && data != null) {
            imagesUri = data.getData();
            isImageAdded = true;
            imageView.setImageURI(imagesUri);
        }
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        String datetime = btn_birthday.getText().toString();

        // Tạo đối tượng SimpleDateFormat để phân tích chuỗi datetime thành ngày, tháng và năm
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date;
        try {
            date = dateFormat.parse(datetime);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

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
                btn_birthday.setText(selectedDate);
                if (!selectedDate.equals(originalbirthday)) {
                    isChangeInfor = true;
                } else {
                    isChangeInfor = false;
                }
            }
        });
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Hủy", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn "Hủy" trên DatePickerDialog
            }
        });
        Calendar minDateCalendar = Calendar.getInstance();
        minDateCalendar.set(Calendar.HOUR_OF_DAY, 0);
        minDateCalendar.set(Calendar.MINUTE, 0);
        minDateCalendar.set(Calendar.SECOND, 0);
        minDateCalendar.set(Calendar.MILLISECOND, 0);
        minDateCalendar.add(Calendar.YEAR, -17);

        Calendar maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.set(Calendar.HOUR_OF_DAY, 23);
        maxDateCalendar.set(Calendar.MINUTE, 59);
        maxDateCalendar.set(Calendar.SECOND, 59);
        maxDateCalendar.set(Calendar.MILLISECOND, 999);
        maxDateCalendar.add(Calendar.YEAR, -40);

        long minDate = maxDateCalendar.getTimeInMillis();
        long maxDate = minDateCalendar.getTimeInMillis();

        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        datePickerDialog.getDatePicker().setMinDate(minDate);


        datePickerDialog.show();
    }

}
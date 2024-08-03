package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.deiteu.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class create_post extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CODE_IMAGE = 101;

    Uri imagesUri;
    private ImageButton btnimg_back;

    private AppCompatButton btn_post;
    private ImageView imgview_addpicture;

    private EditText edt_status;
    private TextView textView_word_count;

    private LinearLayout layout_takephoto, layout_chosepicture;
    private boolean isImageAdded = false;

    private boolean uploadimg = false;
    private ProgressBar progressBar;
    private TextView tvpercent;
    private StorageReference storageref = FirebaseStorage.getInstance().getReference().child("PostImage");

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Post");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        btnimg_back = findViewById(R.id.btnimg_back);
        imgview_addpicture = findViewById(R.id.imgview_addpicture);
        edt_status = findViewById(R.id.edittext_status);
        textView_word_count = findViewById(R.id.textView_word_count);
        btn_post = findViewById(R.id.btn_post);
        //
        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = edt_status.getText().toString().trim();
                final Dialog dialog = new Dialog(create_post.this);
                dialog.setContentView(R.layout.upload_post);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                if (text.length() >= 1) {
                    if (isImageAdded) {
                        uploadPost(dialog, text);
                    } else {
                        final String key = mDatabase.push().getKey();
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", key);
                        hashMap.put("idUser", FirebaseAuth.getInstance().getUid());
                        hashMap.put("status", text);
                        hashMap.put("timecreated", ServerValue.TIMESTAMP);
                        mDatabase.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(create_post.this, "Bài đăng của bạn đã được đăng thành công.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
                            }
                        });
                    }
                } else {
                    if (isImageAdded) {
                        uploadPost(dialog, "");
                    } else {
                        dialog.dismiss();
                        Toast.makeText(create_post.this, "Hãy viết kèm trạng thái hoặc tải lên hình ảnh để đăng khoảnh khắc mới nhé.", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        //
        edt_status.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Đếm số lượng từ và hiển thị trong TextView
//                int wordCount = countWords(s.toString());
                // Đếm số lượng ký tự và hiển thị trong TextView
                int charCount = countCharacters(s.toString());
                textView_word_count.setText(charCount + " / 1000");

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnimg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        imgview_addpicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(create_post.this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_upload_picture);
                layout_chosepicture = dialog.findViewById(R.id.layout_chosepicture);
                layout_takephoto = dialog.findViewById(R.id.layout_takephoto);
                layout_takephoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                        dialog.dismiss();
                    }
                });
                layout_chosepicture.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, REQUEST_CODE_IMAGE);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void uploadPost(Dialog dialog, String status) {
        tvpercent = dialog.findViewById(R.id.tvpercent);
        progressBar = dialog.findViewById(R.id.progressBar);
        tvpercent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        dialog.show();
        final String key = mDatabase.push().getKey();
        storageref.child(key + ".jpg").putFile(imagesUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageref.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", key);
                        if (status.length() > 0) {
                            hashMap.put("status", status);
                        }
                        hashMap.put("idUser", FirebaseAuth.getInstance().getUid());
                        hashMap.put("timecreated", ServerValue.TIMESTAMP);
                        hashMap.put("image", uri.toString());
                        mDatabase.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(create_post.this, "Bài viết đã được đăng.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                finish();
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


    //
    private String getTimeCurrent() {
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        }
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDateTime = dateTime.format(formatter);
        }
        return formattedDateTime;
    }

    //
    private int countWords(String text) {
        int wordCount = 0;
        boolean isWord = false;
        int endOfLine = text.length() - 1;

        for (int i = 0; i < text.length(); i++) {
            // Nếu là ký tự chữ hoặc số thì đánh dấu là đang đếm từ
            if (Character.isLetterOrDigit(text.charAt(i)) && i != endOfLine) {
                isWord = true;
                // Nếu là ký tự không phải chữ hoặc số và đang đếm từ thì tăng biến đếm
            } else if (!Character.isLetterOrDigit(text.charAt(i)) && isWord) {
                wordCount++;
                isWord = false;
                // Nếu là ký tự không phải chữ hoặc số và không đang đếm từ thì bỏ qua
            } else if (Character.isWhitespace(text.charAt(i))) {
                isWord = false;
            }
        }

        // Nếu văn bản kết thúc bằng một từ thì tăng biến đếm
        if (Character.isLetterOrDigit(text.charAt(endOfLine))) {
            wordCount++;
        }

        return wordCount;
    }

    private int countCharacters(String text) {
        return text.length();
    }

    private void dispatchTakePictureIntent() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent, 1);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgview_addpicture.setImageBitmap(imageBitmap);
            imgview_addpicture.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams layoutParams = imgview_addpicture.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imgview_addpicture.setLayoutParams(layoutParams);
            try {
                ConvertBitmapToURI(imageBitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            isImageAdded = true;
        }
        if (requestCode == REQUEST_CODE_IMAGE && data != null) {
            imagesUri = data.getData();
            isImageAdded = true;
            imgview_addpicture.setImageURI(imagesUri);
            ViewGroup.LayoutParams layoutParams = imgview_addpicture.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imgview_addpicture.setLayoutParams(layoutParams);
        }
    }

    //    public void ConvertBitmapToURI(Bitmap imBitmap) throws IOException {
//        // Tạo một đối tượng ByteArrayOutputStream để ghi dữ liệu hình ảnh
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        // Nén hình ảnh thành một định dạng JPEG và ghi dữ liệu vào ByteArrayOutputStream
//        imBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        // Tạo một đối tượng File mới để lưu hình ảnh
//        File imageFile = new File(getExternalCacheDir(), "image.jpg");
//        // Ghi dữ liệu hình ảnh từ ByteArrayOutputStream vào File
//        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
//        fileOutputStream.write(bytes.toByteArray());
//        fileOutputStream.close();
//        // Tạo một đối tượng URI từ đối tượng File
//        imagesUri = Uri.fromFile(imageFile);
//    }
    public void ConvertBitmapToURI(Bitmap imBitmap) throws IOException {
        // Giảm kích thước ảnh
        int maxWidth = 4000; // kích thước tối đa của chiều rộng hoặc chiều cao
        int maxHeight = 4000;
        float scale = Math.min(((float) maxWidth / imBitmap.getWidth()), ((float) maxHeight / imBitmap.getHeight()));
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap scaledBitmap = Bitmap.createBitmap(imBitmap, 0, 0, imBitmap.getWidth(), imBitmap.getHeight(), matrix, true);

        // Tạo một đối tượng ByteArrayOutputStream để ghi dữ liệu hình ảnh
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        // Nén hình ảnh thành một định dạng JPEG và ghi dữ liệu vào ByteArrayOutputStream
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        // Tạo một đối tượng File mới để lưu hình ảnh
        File imageFile = new File(getExternalCacheDir(), "image.jpg");
        // Ghi dữ liệu hình ảnh từ ByteArrayOutputStream vào File
        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
        fileOutputStream.write(bytes.toByteArray());
        fileOutputStream.close();
        // Tạo Uri từ File sử dụng thư viện Glide
        Glide.with(this)
                .load(imageFile)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Lấy Uri từ ảnh hiển thị
                        imagesUri = Uri.fromFile(imageFile);
                        return false;
                    }
                })
                .submit();
    }




}
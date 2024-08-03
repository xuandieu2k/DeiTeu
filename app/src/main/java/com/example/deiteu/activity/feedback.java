package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.Feedback;
import com.example.deiteu.model.FormatNumber;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class feedback extends AppCompatActivity {
    ImageView btnimg_back;
    AppCompatButton btn_send;
    EditText edt_feedback;
    ImageView imgview_addpicture;
    TextView textView_word_count;
    private boolean isImageAdded = false;
    private StorageReference storageref = FirebaseStorage.getInstance().getReference().child("FeedbackImage");

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Feedback");
    private TextView tvpercent;
    private ProgressBar progressBar;
    private Uri imagesUri;
    FormatNumber formatNumber = new FormatNumber();
    List<Feedback> feedbackList = new ArrayList<>();
    private int REQUEST_CODE_IMAGE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        btnimg_back = findViewById(R.id.btnimg_back);
        btn_send = findViewById(R.id.btn_send);
        edt_feedback = findViewById(R.id.edt_feedback);
        imgview_addpicture = findViewById(R.id.imgview_addpicture);
        textView_word_count = findViewById(R.id.textView_word_count);
        imgview_addpicture.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        edt_feedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Đếm số lượng từ và hiển thị trong TextView
//                int wordCount = countWords(s.toString());
                // Đếm số lượng ký tự và hiển thị trong TextView
                int charCount = countCharacters(s.toString());
                textView_word_count.setText(charCount + " / 500");

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
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.clear();
                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot:snapshot.getChildren())
                    {
                        Feedback fb = dataSnapshot.getValue(Feedback.class);
                        String today = formatNumber.convertLongTimetoDateTime(fb.getTimecreated());
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        String gettoday = formatter.format(date);
                        if(fb.getIdUser().equals(FirebaseAuth.getInstance().getUid()) && gettoday.equals(today))
                        {
                            feedbackList.add(fb);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(feedbackList.size()<3)
                {
                    String text = edt_feedback.getText().toString().trim();
                    final Dialog dialog = new Dialog(feedback.this);
                    dialog.setContentView(R.layout.upload_post);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    if (text.length() >= 1) {
                        if (isImageAdded) {
                            uploadFeedBack(dialog, text);
                        } else {
                            final String key = mDatabase.push().getKey();
                            HashMap hashMap = new HashMap();
                            hashMap.put("id", key);
                            hashMap.put("idUser", FirebaseAuth.getInstance().getUid());
                            hashMap.put("feedback", text);
                            hashMap.put("timecreated", ServerValue.TIMESTAMP);
                            mDatabase.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(feedback.this, "Deiteu xin ghi nhận phản hồi của bạn.\nChúng tôi sẽ cố gắng phản hồi bạn sớm nhất có thể", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    } else {
                        if (isImageAdded) {
                            uploadFeedBack(dialog, "");
                        } else {
                            dialog.dismiss();
                            Toast.makeText(feedback.this, "Vui lòng điền đầy đủ thông tin trước khi gửi.", Toast.LENGTH_SHORT).show();

                        }
                    }
                }else{
                    Toast.makeText(feedback.this, "Bạn chỉ có thể gửi tối đa 3 phản hồi mỗi ngày.\nVui lòng quay lại vào ngày mai bạn nhé!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    private int countCharacters(String text) {
        return text.length();
    }
    private void uploadFeedBack(Dialog dialog, String status) {
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
                            hashMap.put("feedback", status);
                        }
                        hashMap.put("idUser", FirebaseAuth.getInstance().getUid());
                        hashMap.put("timecreated", ServerValue.TIMESTAMP);
                        hashMap.put("image", uri.toString());
                        mDatabase.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(feedback.this, "Deiteu xin ghi nhận phản hồi của bạn.\nChúng tôi sẽ cố gắng phản hồi bạn sớm nhất có thể.", Toast.LENGTH_SHORT).show();
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
}
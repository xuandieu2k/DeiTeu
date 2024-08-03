package com.example.deiteu.activity;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.adapter.ListPostAdapter;
import com.example.deiteu.model.Follows;
import com.example.deiteu.model.Post;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;


public class Details_personal extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private AppCompatButton btn_create_post, btn_datejoin,btn_chat;

    private TextView tv_fullname, tv_idU, tv_description, tv_age, tv_text,tv_fullnameUser;

    private ImageView img_background, btn_coppy, imggender;

    private ImageButton btnimg_back;

    private CircleImageView img_avatar;

    private RecyclerView recyclerView;
    private ListPostAdapter listPostAdapter;
    private Users us = new Users();

    String idUserdt = "";

    private DatabaseReference mDatabase;

    private DatabaseReference mdaDatabaseReference;
    Users userCurrent = new Users();
    private DatabaseReference mFollowsDatabase = FirebaseDatabase.getInstance().getReference("Follows");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_personal);
        img_background = findViewById(R.id.img_background);
        img_avatar = findViewById(R.id.img_avatar);
        imggender = findViewById(R.id.imggender);
        tv_age = findViewById(R.id.tv_age);
        tv_text = findViewById(R.id.tv_text);
        btnimg_back = findViewById(R.id.btnimg_back);
        tv_fullnameUser = findViewById(R.id.tv_fullnameUser);
        // recycle view
        btn_chat =findViewById(R.id.btn_chat);
        btn_coppy = findViewById(R.id.imgcoppy_id);
        btn_datejoin = findViewById(R.id.btn_joindate);
        tv_idU = findViewById(R.id.tvidU);
        tv_fullname = findViewById(R.id.tvfullnameU);
        tv_description = findViewById(R.id.desyourself);
        idUserdt = getIntent().getStringExtra("idUserdt");
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(idUserdt);
        btn_chat.setVisibility(View.GONE);
        mFollowsDatabase.child(idUserdt).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) // snapshot.getKey() user duoc follow
                    {
                        boolean isFollowed = false;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Follows fl = snapshot1.getValue(Follows.class);
                            if (fl.getIdFollower().equals(FirebaseAuth.getInstance().getUid())) // da follow
                            {
                                isFollowed = true;
                            }
                        }
                        if (isFollowed) {
                            btn_chat.setVisibility(View.VISIBLE);
                        } else {
                            btn_chat.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btn_coppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo đối tượng ClipboardManager
                ClipboardManager clipboardManager = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);

                // Thiết lập bộ nhớ tạm với đoạn văn bản cần sao chép
                String textToCopy = tv_idU.getText().toString();
                ClipData clipData = ClipData.newPlainText("text", textToCopy);
                // Sao chép dữ liệu vào bộ nhớ tạm
                clipboardManager.setPrimaryClip(clipData);

                // Thông báo cho người dùng biết rằng đoạn văn bản đã được sao chép
                Toast.makeText(getApplicationContext(), "Đã sao chép nội dung", Toast.LENGTH_SHORT).show();

            }
        });
        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), DetailsMessage.class);
                it.putExtra("idReceiver",idUserdt);
                startActivity(it);
            }
        });
        //
        btnimg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userCurrent = snapshot.getValue(Users.class);
                    tv_idU.setText(userCurrent.getId());
                    // avatar
                    if (userCurrent.getAvatar() != null) {
                        Picasso.get().load(userCurrent.getAvatar()).into(img_avatar);
                    }
                    // gender
                    if(userCurrent.getGender() != null)
                    {
                        switch (userCurrent.getGender()) {
                            case "1":
                                imggender.setImageResource(R.drawable.icon_male);
                                break;
                            case "2":
                                imggender.setImageResource(R.drawable.icon_female);
                                break;
                            default: imggender.setImageResource(R.drawable.icon_twogender2);
                        }
                    }
                    // birthday
                    if (userCurrent.getBirthday() != null && !userCurrent.getBirthday().equals("?? / ?? / ????")) {
                        String birthday = userCurrent.getBirthday();
                        int age = calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
                        tv_age.setText(String.valueOf(age));
                    } else {
                        tv_age.setText("");
                    }
                    // img_background
                    if (userCurrent.getBackground() != null) {
                        Picasso.get().load(userCurrent.getBackground()).into(img_background);
                    }
                    // fullname
                    if (userCurrent.getFullname() != null) {
                        tv_fullname.setText(userCurrent.getFullname());
                        tv_fullnameUser.setText(userCurrent.getFullname());
                    } else {
                        tv_fullname.setText("Unknown Name");
                        tv_fullnameUser.setText("Unknown Name");
                    }
                    // descript
                    if (userCurrent.getDescription() != null && !userCurrent.getDescription().equals("")) {
                        tv_description.setText(userCurrent.getDescription());
                    } else {
                        tv_description.setText("Người này là một người bí ẩn.");
                    }
                    // join date
                    if (userCurrent.getJoindate() != null) {
                        joindate = userCurrent.getJoindate();
                        day = joindate.substring(0, 2);
                        month = joindate.substring(3, 5);
                        year = joindate.substring(6, joindate.length());
                        // Sử dụng giá trị  ở đây
                    }
                    if (!joindate.equals("")) {
                        btn_datejoin.setText("Tham gia " + day + " tháng " + month + " năm " + year);
                    } else {
                        btn_datejoin.setText("Tham gia ngày ?? tháng ?? năm ????");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.image_details);
                ImageView img = dialog.findViewById(R.id.photo_view);
                AppCompatButton btn_close = dialog.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                // Lấy đối tượng Window của dialog
                Window window = dialog.getWindow();

                if (window != null) {
                    // Set các thuộc tính cho dialog
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
                    window.setGravity(Gravity.CENTER);
                }
                Picasso.get().load(userCurrent.getAvatar()).into(img);
                dialog.show();
            }
        });
        img_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.image_details);
                ImageView img = dialog.findViewById(R.id.photo_view);
                AppCompatButton btn_close = dialog.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                // Lấy đối tượng Window của dialog
                Window window = dialog.getWindow();

                if (window != null) {
                    // Set các thuộc tính cho dialog
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
                    window.setGravity(Gravity.CENTER);
                }
                Picasso.get().load(userCurrent.getBackground()).into(img);
                dialog.show();
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Post");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    us = snapshot.getValue(Users.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listPostAdapter = new ListPostAdapter(getApplicationContext());
        recyclerView = findViewById(R.id.recycle_listpost);
        recyclerView.setAdapter(listPostAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
//        recyclersetLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setNestedScrollingEnabled(false); // Mượt
        recyclerView.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        //
        Query query = FirebaseDatabase.getInstance().getReference("Post").orderByChild("idUser").equalTo(idUserdt);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPostAdapter.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post.getIdUser().equals(idUserdt)) {
                        post.setUserPost(us);
                        listPostAdapter.add(post);
                    }
                    listPostAdapter.reverse();
                    if(listPostAdapter.getItemCount() > 0)
                    {
                        btn_chat.setVisibility(View.GONE);
                    }else{
                        btn_chat.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    public static int calculateAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        birthdate.set(year, month, day);
        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
    String day, month, year, joindate = "";
}
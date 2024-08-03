package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.adapter.ListPostAdapter;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.Comments;
import com.example.deiteu.model.Follows;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.model.Interact;
import com.example.deiteu.adapter.ListCommentAdapter;
import com.example.deiteu.model.Post;
import com.example.deiteu.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Details_Post extends AppCompatActivity {
    private static final int REQUEST_CODE_IMAGE = 101;

    private RecyclerView recycle_comment;
    private TextView tv_sumlove, tv_sumcomment, tv_age;
    private ImageView imgpost, img_addimage, img_send, imgview_picture;
    private TextView tvtextpost, tvfullnameUser, tv_messages;
    private ImageButton imgbtn_gender, btncomment, btnlove, imgbtn_removepic,btnimg_back;
    private CircleImageView circle_avatar;

    private RecyclerView recycleview;
    private EditText edt_comment;

    private ImageView imgbtn_seemore;
    private AppCompatButton btn_follow_chat;

    private View line;

    private LinearLayout layout_addPic;

    private ListCommentAdapter listCommentAdapter;
    private DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference mCommentDatabase = FirebaseDatabase.getInstance().getReference("Comments");
    private DatabaseReference mInteractDatabase = FirebaseDatabase.getInstance().getReference("Interact");

    private DatabaseReference mFollowsDatabase = FirebaseDatabase.getInstance().getReference("Follows");
    public DatabaseReference mBlacklistDatabase = FirebaseDatabase.getInstance().getReference("Blacklist");

    private DatabaseReference databaseReference;

    private StorageReference storageref = FirebaseStorage.getInstance().getReference().child("CommentsImage");
    private Uri imagesUri;
    private boolean isImageAdded = false;

    private FormatNumber formatNumber = new FormatNumber();
    Post post = new Post();
    String idUserPost;
    private DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance().getReference("Notification");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_post);

        tv_sumlove = findViewById(R.id.tv_sumlove);
        tv_sumcomment = findViewById(R.id.tv_sumcomment);
        tvtextpost = findViewById(R.id.tvtextpost);
        tv_age = findViewById(R.id.tv_age);
        tvfullnameUser = findViewById(R.id.tvfullnameUser);
        imgpost = findViewById(R.id.imgpost);
        circle_avatar = findViewById(R.id.circle_avatar);
        imgbtn_gender = findViewById(R.id.imgbtn_gender);
        btnlove = findViewById(R.id.btnlove);
        btncomment = findViewById(R.id.btncomment);
        btnimg_back = findViewById(R.id.btnimg_back);

        edt_comment = findViewById(R.id.edt_comment);
        img_addimage = findViewById(R.id.img_addimage);
        img_send = findViewById(R.id.img_send);
        tv_messages = findViewById(R.id.tv_messages);
        // layout add picture
        layout_addPic = findViewById(R.id.layout_addPic);
        imgview_picture = findViewById(R.id.imgview_picture);
        imgbtn_removepic = findViewById(R.id.imgbtn_removepic);
        layout_addPic.setVisibility(View.GONE);
        line = findViewById(R.id.line);
        btn_follow_chat = findViewById(R.id.btn_follow_chat);
        imgbtn_seemore = findViewById(R.id.imgbtn_seemore);
        btn_follow_chat.setVisibility(View.VISIBLE);
        imgbtn_seemore.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference("Post");

        listCommentAdapter = new ListCommentAdapter(getApplicationContext());
        recycleview = findViewById(R.id.recycle_comment);
        recycleview.setAdapter(listCommentAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recycleview.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleview.setHasFixedSize(true);
        recycleview.setItemViewCacheSize(20);
        recycleview.setDrawingCacheEnabled(true);
        recycleview.setNestedScrollingEnabled(false); // Mượt
        recycleview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycleview.setVisibility(View.GONE);
        tv_messages.setText("Hãy trở thành người đầu tiên\n bình luận bài viết này nào.");
        //
        // Đăng ký một AdapterDataObserver
        Objects.requireNonNull(recycleview.getAdapter()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (recycleview.getAdapter().getItemCount() > 0) {
                    tv_messages.setVisibility(View.GONE);
                    recycleview.setVisibility(View.VISIBLE);
                } else {
                    tv_messages.setVisibility(View.VISIBLE);
                }
            }
        });
        //
        btnimg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        String idPost = intent.getStringExtra("keyidpost");
        idUserPost = intent.getStringExtra("keyidUserpost");
        getListU();
        databaseReference.child(idPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    post = snapshot.getValue(Post.class);
                    if (post.getImage() != null) {
                        Picasso.get().load(post.getImage()).into(imgpost);
                    } else {
                        imgpost.setVisibility(View.GONE);
                    }
                    if (post.getStatus() != null) {
                        tvtextpost.setText(post.getStatus());
                    } else {
                        tvtextpost.setVisibility(View.GONE);
                    }
                    mUserDatabase.child(post.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Users us = snapshot.getValue(Users.class);
                                post.setUserPost(us);
                                if (post.getUserPost().getAvatar() != null) {
                                    Picasso.get().load(post.getUserPost().getAvatar()).into(circle_avatar);
                                }
                                if (post.getUserPost().getFullname() != null) {
                                    tvfullnameUser.setText(post.getUserPost().getFullname());
                                }
                                if (post.getUserPost().getGender() != null) {
                                    switch (post.getUserPost().getGender()) {
                                        case "0":
                                            imgbtn_gender.setImageResource(R.drawable.icon_twogender2);
                                            break;
                                        case "1":
                                            imgbtn_gender.setImageResource(R.drawable.icon_male);
                                            imgbtn_gender.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.green_custom), PorterDuff.Mode.SRC_IN);
                                            break;
                                        case "2":
                                            imgbtn_gender.setImageResource(R.drawable.icon_female);
                                            imgbtn_gender.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.pink_custom), PorterDuff.Mode.SRC_IN);
                                            break;
                                        default:
                                    }
                                }
                                if (post.getUserPost().getBirthday() != null) {
                                    String birthday = post.getUserPost().getBirthday();
                                    int age = calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
                                    tv_age.setText(String.valueOf(age));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mCommentDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                long count = snapshot.getChildrenCount();
                                tv_sumcomment.setText(""+formatNumber.formatFollowersCount((int) count));
                            }else{
                                tv_sumlove.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mInteractDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {
                                long count = snapshot.getChildrenCount();
                                tv_sumlove.setText(""+formatNumber.formatFollowersCount((int) count));
                            }else{
                                tv_sumlove.setText("0");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        mCommentDatabase.child(idPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listCommentAdapter.clear();
                    for (DataSnapshot commentsnap : snapshot.getChildren()) {
                        Comments comments = commentsnap.getValue(Comments.class);
                        mUserDatabase.child(comments.getIdUser()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        Users us = snapshot.getValue(Users.class);
                                        comments.setUserComnent(us);
                                        listCommentAdapter.add(comments);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        circle_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(Details_Post.this,Details_personal.class);
                it.putExtra("idUserdt", post.getIdUser());
                startActivity(it);
            }
        });
        img_addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        img_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"WrongConstant", "ShowToast"})
            @Override
            public void onClick(View view) {
                if (!edt_comment.getText().toString().equals("")) {
//                    Toast.makeText(Details_Post.this, "Đang gửi bình luận...", 50).show();
                    String text = edt_comment.getText().toString();
                    if (isImageAdded) {
//                        String id = String.valueOf(UUID.randomUUID());
//                        final String key = mDatabase.push().getKey();
                        postCommnets(text, idPost);
                    } else {
                        final String key = databaseReference.push().getKey();
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", key);
                        hashMap.put("content", text);
                        hashMap.put("idPost", idPost);
                        hashMap.put("idUser", FirebaseAuth.getInstance().getUid());
                        hashMap.put("timecreated", getTimeCurrent());
                        mCommentDatabase.child(idPost).child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(Details_Post.this, "Đã gửi bình luận", Toast.LENGTH_SHORT).show();
                                addNotification(idUserPost,idPost);
                                edt_comment.setText("");
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Details_Post.this, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    if (isImageAdded) {
                        postCommnets("", idPost);
                    }
                }
            }
        });
        imgpost.setOnClickListener(new View.OnClickListener() {
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
                Picasso.get().load(post.getImage()).into(img);
                dialog.show();
            }
        });
        imgbtn_removepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_addPic.setVisibility(View.GONE);
                imgbtn_removepic.setVisibility(View.GONE);
                imgview_picture.setVisibility(View.GONE);
                imgview_picture.setImageURI(null);
                isImageAdded = false;
                imagesUri = null;
            }
        });
        String idUserCurrent = FirebaseAuth.getInstance().getUid();
        // check follow
        mFollowsDatabase.child(idUserPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getKey().equals(idUserCurrent)) // snapshot.getKey() user duoc follow
                    {
                        btn_follow_chat.setVisibility(View.VISIBLE);
                        imgbtn_seemore.setVisibility(View.GONE);
                        boolean isFollowed = false;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Follows fl = snapshot1.getValue(Follows.class);
                            if (fl.getIdFollower().equals(idUserCurrent)) // da follow
                            {
                                isFollowed = true;
                            }
                        }
                        if(isFollowed)
                        {
                            btn_follow_chat.setText("Nhắn tin");
                        } else {
                            btn_follow_chat.setText("Theo dõi");
                        }

                    } else {
                        btn_follow_chat.setVisibility(View.GONE);
                        imgbtn_seemore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // check show button follow or chat
        if(idUserCurrent.equals(idUserPost))
        {
            btn_follow_chat.setVisibility(View.GONE);
            imgbtn_seemore.setVisibility(View.GONE);
        }
        btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = btn_follow_chat.getText().toString();
                if (text.equals("Nhắn tin")) {
                    Intent it = new Intent(view.getContext(), DetailsMessage.class);
                    it.putExtra("idReceiver", idUserPost);
                    view.getContext().startActivity(it);
                } else {
                    HashMap hash = new HashMap();
                    String radomid = UUID.randomUUID().toString();
                    hash.put("id",radomid);
                    hash.put("idFollower",idUserCurrent);
                    hash.put("timeFollow",getTimeCurrent());
                    mFollowsDatabase.child(idUserPost).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Bạn đang theo dõi người này.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        // Tuong tac tha tim
        mInteractDatabase.child(idPost).addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Interact interact = dataSnapshot.getValue(Interact.class);
                        if(interact.getIdUser().equals(idUserCurrent))
                        {
                            btnlove.setBackground(getApplicationContext().getDrawable(R.drawable.icon_lovefull));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnlove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap hash = new HashMap();
                String radomid = UUID.randomUUID().toString();
                hash.put("id",radomid);
                hash.put("idUser",idUserCurrent);
                hash.put("idPost",idPost);
                hash.put("timecreated",getTimeCurrent());
                mInteractDatabase.child(idPost).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            Interact interact = new Interact();
                            boolean isInterect = false;
                            for (DataSnapshot dt  :snapshot.getChildren())
                            {
                                Interact it = dt.getValue(Interact.class);
                                if(it.getIdUser().equals(idUserCurrent))
                                {
                                    isInterect = true;
                                    interact = it;
                                }
                            }
                            if(isInterect)
                            {
                                mInteractDatabase.child(idPost).child(interact.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        btnlove.setBackground(getApplicationContext().getDrawable(R.drawable.icon_love));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Đã có lỗi xảy ra.\nVui lòng thử lại", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                mInteractDatabase.child(idPost).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint("UseCompatLoadingForDrawables")
                                    @Override
                                    public void onSuccess(Void unused) {
                                        btnlove.setBackground(getApplicationContext().getDrawable(R.drawable.icon_lovefull));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Đã có lỗi xảy ra.\nVui lòng thử lại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }else{
                            mInteractDatabase.child(idPost).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                @Override
                                public void onSuccess(Void unused) {
                                    btnlove.setBackground(getApplicationContext().getDrawable(R.drawable.icon_lovefull));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Đã có lỗi xảy ra.\nVui lòng thử lại", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    List<String> idStringBlackListReceiver = new ArrayList<>();
    List<String> idStringBlackListSender = new ArrayList<>();

    public void getListU() {
        idStringBlackListReceiver.clear();
        idStringBlackListSender.clear();
        String idU = FirebaseAuth.getInstance().getUid();
        assert idU != null;
        mBlacklistDatabase.child(idU).addValueEventListener(new ValueEventListener() { // Sender
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Blacklist follows = data.getValue(Blacklist.class);
                        idStringBlackListSender.add(follows.getIdUser());
                    }
                }
                mBlacklistDatabase.child(idUserPost).addValueEventListener(new ValueEventListener() { // Receiver
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Blacklist follows = data.getValue(Blacklist.class);
                                idStringBlackListReceiver.add(follows.getIdUser());
                            }
                        }
                        if (idStringBlackListReceiver.contains(idU) || idStringBlackListSender.contains(idUserPost)) {
                            img_send.setClickable(false);
                            edt_comment.setEnabled(false);
                            img_addimage.setClickable(false);
                            img_send.setBackground(getDrawable(R.drawable.box_icon_disable));
                            img_addimage.setBackground(getDrawable(R.drawable.box_icon_disable));
                            edt_comment.setBackground(getDrawable(R.drawable.box_disabled_comment));
                            btncomment.setImageTintList(getColorStateList(R.color.gray2_custom));
                            btnlove.setImageTintList(getColorStateList(R.color.gray2_custom));
                            btncomment.setClickable(false);
                            btnlove.setClickable(false);
                            edt_comment.setHint("Không thể gửi bình luận.");
                        } else {
                            img_send.setClickable(true);
                            edt_comment.setEnabled(true);
                            img_addimage.setClickable(true);
                            img_send.setBackground(getDrawable(R.drawable.box_icon_addimage));
                            img_addimage.setBackground(getDrawable(R.drawable.box_icon_addimage));
                            edt_comment.setBackground(getDrawable(R.drawable.box_edit_comment));
                            btncomment.setImageTintList(getColorStateList(R.color.green_custom));
                            btnlove.setImageTintList(getColorStateList(R.color.green_custom));
                            btncomment.setClickable(true);
                            btnlove.setClickable(true);
                            edt_comment.setHint("Nhắn tin");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void addNotification(String idUserPost, String idPost) {
        String idUCurrent = FirebaseAuth.getInstance().getUid();
        if(!idUserPost.equals(idUCurrent))
        {
            HashMap hash = new HashMap<>();
            String radomid = UUID.randomUUID().toString();
            hash.put("id", radomid);
            hash.put("idPost", idPost);
            hash.put("idUser", idUCurrent);
            hash.put("content", "Đã bình luận bài biết của bạn");
            hash.put("readed", false);
            hash.put("type", "2");
            hash.put("timeCreated", ServerValue.TIMESTAMP);
            mNotificatonDatabase.child(idUserPost).child(radomid).setValue(hash);
        }
    }


    private void postCommnets(String content, String idPost) {
        final String key = databaseReference.push().getKey();
        storageref.child(key + ".jpg").putFile(imagesUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressLint({"WrongConstant", "ShowToast"})
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageref.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Toast.makeText(Details_Post.this, "Đang gửi bình luận...", 200).show();
                        HashMap hashMap = new HashMap();
                        hashMap.put("id", key);
                        if (content.length() > 0) {
                            hashMap.put("content", content);
                        }
                        hashMap.put("idPost", idPost);
                        hashMap.put("idUser", FirebaseAuth.getInstance().getUid());
                        hashMap.put("image", uri.toString());
                        hashMap.put("timecreated", getTimeCurrent());
                        mCommentDatabase.child(idPost).child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                layout_addPic.setVisibility(View.GONE);
                                imgbtn_removepic.setVisibility(View.GONE);
                                imgview_picture.setVisibility(View.GONE);
                                imgview_picture.setImageURI(null);
                                isImageAdded = false;
                                imagesUri = null;
                                Toast.makeText(Details_Post.this, "Bình luận thành công.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Details_Post.this, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && data != null) {
            imagesUri = data.getData();
            isImageAdded = true;
            imgview_picture.setImageURI(imagesUri);
            layout_addPic.setVisibility(View.VISIBLE);
            imgview_picture.setVisibility(View.VISIBLE);
            imgbtn_removepic.setVisibility(View.VISIBLE);
        }
    }

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
}
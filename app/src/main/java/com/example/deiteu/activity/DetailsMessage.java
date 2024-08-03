package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.CallRoom;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.adapter.ListMessageAdapter;
import com.example.deiteu.model.Message;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DetailsMessage extends AppCompatActivity {
    private String receivedId;

    String senderRoom, receiverRoom;
    private DatabaseReference mdataRefSender, mdataRefReceiver;


    private ListMessageAdapter listMessageAdapter;

    private RecyclerView recylemessage;

    private ImageButton btnback, imgbtn_removepic, callvideo_button, call_button;

    private ImageView img_addimage, img_send, imgview_picture, img_avatardtchat, img_active;
    private LinearLayout layout_addPic;
    private EditText edt_comment;
    private TextView tvNameChat, tvstatusUser;
    private int REQUEST_CODE_IMAGE = 101;
    private Uri imagesUri;
    private boolean isImageAdded = false;
    private boolean checkOpenActi = false; // Kiểm tra activiti có đang mở
    FormatNumber formatNumber = new FormatNumber();
    Users userChat = new Users();
    public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");

    private StorageReference storageref = FirebaseStorage.getInstance().getReference().child("MessageImage");
    private DatabaseReference mCallDatabase = FirebaseDatabase.getInstance().getReference("CallRoom");
    private DatabaseReference mStatusDatabase = FirebaseDatabase.getInstance().getReference("Status");
    private SharedPreferences sharedPreferences;
    private boolean checkOnline = false;
    public DatabaseReference mBlacklistDatabase = FirebaseDatabase.getInstance().getReference("Blacklist");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_message);
        sharedPreferences = getApplicationContext().getSharedPreferences("idCaller", Context.MODE_PRIVATE);
        // Lưu trữ dữ liệu
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //
        checkOpenActi = true;
        receivedId = getIntent().getStringExtra("idReceiver");
        senderRoom = FirebaseAuth.getInstance().getUid() + receivedId;
        receiverRoom = receivedId + FirebaseAuth.getInstance().getUid();

        tvNameChat = findViewById(R.id.tvNameChat);
        img_avatardtchat = findViewById(R.id.img_avatardtchat);
        img_active = findViewById(R.id.img_active);
        tvstatusUser = findViewById(R.id.tvstatusUser);
        call_button = findViewById(R.id.call_button);
        callvideo_button = findViewById(R.id.callvideo_button);
        //
        mUserDatabase.child(receivedId).keepSynced(false);
        mUserDatabase.child(receivedId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userChat = snapshot.getValue(Users.class);
                    if (userChat.getAvatar() != null) {
                        Picasso.get().load(userChat.getAvatar()).into(img_avatardtchat);
                    }
                    if (userChat.getFullname() != null) {
                        tvNameChat.setText(userChat.getFullname());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mUserDatabase.child(receivedId).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean online = snapshot.getValue(Boolean.class);
                if (online) {
                    img_active.setVisibility(View.VISIBLE);
                    tvstatusUser.setText("Đang hoạt động");
                    checkOnline = true;
                } else {
                    img_active.setVisibility(View.GONE);
                    tvstatusUser.setText("" + formatNumber.getTimeAgo(userChat.getTimelastonline()));
                    checkOnline = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        img_avatardtchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(view.getContext(), Details_personal.class);
                it.putExtra("idUserdt", receivedId);
                it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                view.getContext().startActivity(it);
            }
        });
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap hashMap = new HashMap();
                String id = UUID.randomUUID().toString();
                hashMap.put("id", id);
                hashMap.put("calling", true);
                hashMap.put("idCaller", FirebaseAuth.getInstance().getUid());
                hashMap.put("callingVideo", false);
                hashMap.put("acceptCall", false);
                hashMap.put("refuseCall", false);
                hashMap.put("end", false);
                String userid = FirebaseAuth.getInstance().getUid();
                assert userid != null;
                mCallDatabase.child(receivedId).keepSynced(false);
                mCallDatabase.child(receivedId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int countCallingVoice = 0;
                                checkExistCallerid = false;

                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        CallRoom callRoom = dataSnapshot.getValue(CallRoom.class);
                                        assert callRoom != null;
                                        if (callRoom.getIdCaller().equals(userid)) {
                                            checkExistCallerid = true;
                                            idroom = callRoom.getId();
                                        }
                                        if (callRoom.isCalling() || callRoom.isCallingVideo()) {
                                            countCallingVoice++;
                                        }
                                    }
                                }
                                boolean checkStatus = checkStatusVoiceCallUser(receivedId);
                                if (checkOnline) // Check User Receiver have online?
                                {
                                    // ############
                                    if (!checkStatus) {
                                        if (countCallingVoice < 1) {
                                            if (checkExistCallerid) {
                                                mCallDatabase.child(receivedId).keepSynced(false);
                                                mCallDatabase.child(receivedId).child(idroom).child("calling").setValue(true);
                                                mStatusDatabase.child(userid).keepSynced(false);
                                                mStatusDatabase.child(userid).setValue(true);
                                                Intent intent = new Intent(getApplicationContext(), Call.class);
                                                intent.putExtra("id", idroom);
                                                intent.putExtra("idCaller", userid);
                                                intent.putExtra("idRecicer", receivedId);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
                                                mCallDatabase.child(receivedId).keepSynced(false);
                                                mCallDatabase.child(receivedId).child(id).setValue(hashMap);
                                                mStatusDatabase.child(userid).keepSynced(false);
                                                mStatusDatabase.child(userid).setValue(true);
                                                Intent intent = new Intent(getApplicationContext(), Call.class);
                                                intent.putExtra("id", id);
                                                intent.putExtra("idCaller", userid);
                                                intent.putExtra("idRecicer", receivedId);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(DetailsMessage.this, "Người này đang có cuộc gọi khác", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(DetailsMessage.this, "Người này đang có cuộc gọi khác", Toast.LENGTH_SHORT).show();
                                    }
                                    // ############

                                } else {
                                    Toast.makeText(DetailsMessage.this, "Người dùng hiện tại không online", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        callvideo_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap hashMap = new HashMap();
                String id = UUID.randomUUID().toString();
                hashMap.put("id", id);
                hashMap.put("calling", false);
                hashMap.put("idCaller", FirebaseAuth.getInstance().getUid());
                hashMap.put("callingVideo", true);
                hashMap.put("acceptCall", false);
                hashMap.put("refuseCall", false);
                hashMap.put("end", false);
                String userid = FirebaseAuth.getInstance().getUid();
                assert userid != null;
                mCallDatabase.child(receivedId).keepSynced(false);
                mCallDatabase.child(receivedId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int countCallingVideo = 0;
                                checkExistCallerid = false;
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        CallRoom callRoom = dataSnapshot.getValue(CallRoom.class);
                                        assert callRoom != null;
                                        if (callRoom.getIdCaller().equals(userid)) {
                                            checkExistCallerid = true;
                                            idroom = callRoom.getId();
                                        }
                                        if (callRoom.isCalling() || callRoom.isCallingVideo()) {
                                            countCallingVideo++;
                                        }
                                    }
                                }
                                boolean checkStatus = checkStatusVideoCallUser(receivedId);
                                if (checkOnline) {
                                    // #####
                                    if (!checkStatus) {
                                        if (countCallingVideo < 1) {
                                            if (checkExistCallerid) {
//                                                mCallDatabase.child(receivedId).keepSynced(false);
                                                mCallDatabase.child(receivedId).child(idroom).child("callingVideo").setValue(true);
//                                                mStatusDatabase.child(userid).keepSynced(false);
                                                mStatusDatabase.child(userid).setValue(true);
                                                Intent intent = new Intent(getApplicationContext(), VideoCall.class);
                                                intent.putExtra("id", idroom);
                                                intent.putExtra("idCaller", userid);
                                                intent.putExtra("idRecicer", receivedId);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            } else {
//                                                mCallDatabase.child(receivedId).keepSynced(false);
                                                mCallDatabase.child(receivedId).child(id).setValue(hashMap);
//                                                mStatusDatabase.child(userid).keepSynced(false);
                                                mStatusDatabase.child(userid).setValue(true);
                                                Intent intent = new Intent(getApplicationContext(), VideoCall.class);
                                                intent.putExtra("id", id);
                                                intent.putExtra("idCaller", userid);
                                                intent.putExtra("idRecicer", receivedId);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(DetailsMessage.this, "Người này đang có cuộc gọi khác", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(DetailsMessage.this, "Người này đang có cuộc gọi khác", Toast.LENGTH_SHORT).show();
                                    }
                                    // #####
                                } else {
                                    Toast.makeText(DetailsMessage.this, "Người dùng hiện tại không online", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });
        edt_comment = findViewById(R.id.edt_comment);
        edt_comment.setHint("Nhắn tin");
        img_addimage = findViewById(R.id.img_addimage);
        img_send = findViewById(R.id.img_send);
        // layout add picture
        layout_addPic = findViewById(R.id.layout_addPic);
        imgview_picture = findViewById(R.id.imgview_picture);
        imgbtn_removepic = findViewById(R.id.imgbtn_removepic);
        layout_addPic.setVisibility(View.GONE);
        btnback = findViewById(R.id.back_button);
        //
        // show text and button send
        getListU();

        listMessageAdapter = new ListMessageAdapter(this);
        recylemessage = findViewById(R.id.recylemessage);
        recylemessage.setAdapter(listMessageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recylemessage.setLayoutManager(layoutManager);
        recylemessage.scrollToPosition(listMessageAdapter.getItemCount() - 1);
        mdataRefSender = FirebaseDatabase.getInstance().getReference("Message").child(senderRoom);
        mdataRefReceiver = FirebaseDatabase.getInstance().getReference("Message").child(receiverRoom);
        mdataRefSender.orderByChild("timeSend").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessageAdapter.clear();
//                List<Message> messageList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    com.example.deiteu.model.Message message = dataSnapshot.getValue(Message.class);
                    assert message != null;
                    if (!message.isReaded() && checkOpenActi) {
                        mdataRefSender.child(message.getMsgId()).child("readed").setValue(true);
                    }
                    listMessageAdapter.add(message);
                }
                listMessageAdapter.notifyDataSetChanged();
                recylemessage.scrollToPosition(listMessageAdapter.getItemCount() - 1);
//                Collections.reverse(messageList);
//                listMessageAdapter.addAll(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        img_send.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"WrongConstant", "ShowToast"})
            @Override
            public void onClick(View view) {
                img_send.setClickable(false);
                if (!edt_comment.getText().toString().equals("")) {
//                    Toast.makeText(DetailsMessage.this, "Đang gửi ...", 100).show();
                    String text = edt_comment.getText().toString();
                    if (isImageAdded) {
//                        String id = String.valueOf(UUID.randomUUID());
//                        final String key = mDatabase.push().getKey();
                        sendMessages(text);
                    } else {
                        final String key = UUID.randomUUID().toString();
                        HashMap hashMap = new HashMap();
                        hashMap.put("msgId", key);
                        hashMap.put("message", text);
                        hashMap.put("senderId", FirebaseAuth.getInstance().getUid());
                        hashMap.put("timeSend", ServerValue.TIMESTAMP);
                        hashMap.put("readed", false);
                        mdataRefSender.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                edt_comment.setText("");
                                img_send.setClickable(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(DetailsMessage.this, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mdataRefReceiver.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
//                                Toast.makeText(DetailsMessage.this, "Bình luận thành công.", Toast.LENGTH_SHORT).show();
                                edt_comment.setText("");
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(DetailsMessage.this, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    if (isImageAdded) {
                        sendMessages("");
                    }
                }
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

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOpenActi = false;
                finish();
            }
        });
    }

    boolean checkExistCallerid = false;
    String idroom = "";
    int countCalling = 0;
    boolean checkFinished = false;

    private boolean checkStatusVoiceCallUser(String idReceiver) {
//        checkStatusCallVoice = false;
        final boolean[] checkCallVoice = {false};
        mStatusDatabase.child(idReceiver).keepSynced(false);
        mStatusDatabase.child(idReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkCallVoice[0] = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                } else {
                    checkCallVoice[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return checkCallVoice[0];
    }

    private boolean checkStatusVideoCallUser(String idReceiver) {
//        checkStatusCallVideo = false;
        final boolean[] checkCallVideo = {false};
        mStatusDatabase.child(idReceiver).keepSynced(false);
        mStatusDatabase.child(idReceiver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    checkCallVideo[0] = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                } else {
                    checkCallVideo[0] = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return checkCallVideo[0];
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
                mBlacklistDatabase.child(receivedId).addValueEventListener(new ValueEventListener() { // Receiver
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Blacklist follows = data.getValue(Blacklist.class);
                                idStringBlackListReceiver.add(follows.getIdUser());
                            }
                        }
                        if (idStringBlackListReceiver.contains(idU) || idStringBlackListSender.contains(receivedId)) {
                            img_send.setClickable(false);
                            edt_comment.setEnabled(false);
                            img_addimage.setClickable(false);
                            img_send.setBackground(getDrawable(R.drawable.box_icon_disable));
                            img_addimage.setBackground(getDrawable(R.drawable.box_icon_disable));
                            edt_comment.setBackground(getDrawable(R.drawable.box_disabled_comment));
                            call_button.setImageTintList(getColorStateList(R.color.gray2_custom));
                            callvideo_button.setImageTintList(getColorStateList(R.color.gray2_custom));
                            call_button.setClickable(false);
                            callvideo_button.setClickable(false);
                            edt_comment.setHint("Không thể gửi tin nhắn.");
                        } else {
                            img_send.setClickable(true);
                            edt_comment.setEnabled(true);
                            img_addimage.setClickable(true);
                            img_send.setBackground(getDrawable(R.drawable.box_icon_addimage));
                            img_addimage.setBackground(getDrawable(R.drawable.box_icon_addimage));
                            edt_comment.setBackground(getDrawable(R.drawable.box_edit_comment));
                            call_button.setImageTintList(getColorStateList(R.color.white));
                            callvideo_button.setImageTintList(getColorStateList(R.color.white));
                            call_button.setClickable(true);
                            callvideo_button.setClickable(true);
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

    private void SendMessages(String mess) {
//        String radomid = UUID.randomUUID().toString();
//        Message message = new Message(radomid, FirebaseAuth.getInstance().getUid(), mess, "");
//        listMessageAdapter.add(message);
//        mdataRefSender.child(radomid).setValue(message);
//        mdataRefReceiver.child(radomid).setValue(message);
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

    boolean postSender = false;
    boolean postReceiver = false;

    private void sendMessages(String content) {
        final String key = UUID.randomUUID().toString();
        storageref.child(key + ".jpg").putFile(imagesUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @SuppressLint({"WrongConstant", "ShowToast"})
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                edt_comment.setText("");
                storageref.child(key + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Toast.makeText(Details_Post.this, "Đang gửi bình luận...", 200).show();
                        HashMap hashMap = new HashMap();
                        hashMap.put("msgId", key);
                        if (content.length() > 0) {
                            hashMap.put("message", content);
                        }
                        hashMap.put("senderId", FirebaseAuth.getInstance().getUid());
                        hashMap.put("image", uri.toString());
                        hashMap.put("timeSend", ServerValue.TIMESTAMP);
                        hashMap.put("readed", false);
                        mdataRefSender.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                layout_addPic.setVisibility(View.GONE);
                                imgbtn_removepic.setVisibility(View.GONE);
                                imgview_picture.setVisibility(View.GONE);
                                imgview_picture.setImageURI(null);
                                isImageAdded = false;
                                imagesUri = null;
                                postSender = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                postSender = false;
                            }
                        });
                        //
                        mdataRefReceiver.child(key).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                layout_addPic.setVisibility(View.GONE);
                                imgbtn_removepic.setVisibility(View.GONE);
                                imgview_picture.setVisibility(View.GONE);
                                imgview_picture.setImageURI(null);
                                isImageAdded = false;
                                imagesUri = null;
                                postReceiver = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                postSender = false;
                            }
                        });
                        //
                        img_send.setClickable(true);
                    }
                });
            }
        });
    }
}
package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.example.deiteu.R;
import com.example.deiteu.model.CallRoom;
import com.example.deiteu.model.GenerateTokenAgora;
import com.example.deiteu.model.Users;
import com.example.deiteu.service.CallRoomService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.ChannelMediaOptions;


public class Call extends AppCompatActivity {
    // Fill the App ID of your project generated on Agora Console.
    private String appId = "";
    // Fill the channel name.
    private String channelName = "";
    // Fill the temp token generated on Agora Console.
    private String token = "";
    // An integer that identifies the local user.
    private String appCertificateCall = "";
    private int uid = 0;
    // Track the status of your connection
    private boolean isJoined = false;

    // Agora engine instance
    private RtcEngine agoraEngine;
    // UI elements
    private TextView tv_infor;
    CircleImageView circle_avatar;
    private CircleImageView btnaccept, btnrefuse, img_voice, btnend, btnleave;
    String id, idCaller, idRecicer;
    LinearLayout layout_receiver, layout_caller, layout_joined;
    public DatabaseReference mCallDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("CallRoom");
    public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
    Users Caller = new Users();
    Users Receiver = new Users();
    private CountDownTimer callTimer;
    private long callStartTime;
    private String sumTimeCall = "";
    private GenerateTokenAgora generateTokenAgora;
    private void setupVoiceSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
        } catch (Exception e) {
            throw new RuntimeException("Check the error.");
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    public void startCountTimeCall()
    {
        callStartTime = System.currentTimeMillis();
        callTimer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long callDuration = System.currentTimeMillis() - callStartTime;
                @SuppressLint("DefaultLocale") String callDurationFormatted = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(callDuration),
                        TimeUnit.MILLISECONDS.toSeconds(callDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(callDuration)));
                tv_infor.setText(callDurationFormatted);
            }

            @Override
            public void onFinish() {
                // Do nothing
                sumTimeCall = tv_infor.getText().toString();
            }
        };
        callTimer.start();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        generateTokenAgora = new GenerateTokenAgora();
        appId = getApplicationContext().getString(R.string.appIdCall);
        appCertificateCall = getApplicationContext().getString(R.string.appCertificateCall);

        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVoiceSDKEngine();
        // Set up access to the UI elements
        layout_caller = findViewById(R.id.layout_caller);
        btnend = findViewById(R.id.btnend);
        //
        layout_joined = findViewById(R.id.layout_joined);
        btnleave = findViewById(R.id.btnleave);
        img_voice = findViewById(R.id.img_voice);
        //
        layout_receiver = findViewById(R.id.layout_receiver);
        btnrefuse = findViewById(R.id.btnrefuse);
        btnaccept = findViewById(R.id.btnaccept);
        //
        tv_infor = findViewById(R.id.tv_infor);
        circle_avatar = findViewById(R.id.circle_avatar);
        id = getIntent().getStringExtra("id");
        idRecicer = getIntent().getStringExtra("idRecicer");
        idCaller = getIntent().getStringExtra("idCaller");
        channelName  = idCaller+idRecicer;
        token = generateTokenAgora.GenerateTokenVoiceCall(appId,appCertificateCall,channelName,uid);
        //
        layout_joined.setVisibility(View.GONE);
        layout_receiver.setVisibility(View.GONE);
        layout_caller.setVisibility(View.GONE);
        // Lưu trữ dữ liệu
        mUserDatabase.child(idCaller).keepSynced(true);
        mUserDatabase.child(idCaller).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Caller = snapshot.getValue(Users.class);
                mUserDatabase.child(idCaller).keepSynced(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        mUserDatabase.child(idRecicer).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Receiver = snapshot.getValue(Users.class);
                if (!FirebaseAuth.getInstance().getUid().equals(idCaller)) {
                    // Receiver
                    if (Caller.getFullname() != null) {
                        tv_infor.setText("Cuộc gọi từ " + Caller.getFullname());
                    } else {
                        tv_infor.setText("Cuộc gọi từ Unknown");
                    }
                    if (Caller.getAvatar() != null) {
                        Picasso.get().load(Caller.getAvatar()).into(circle_avatar);
                    }
                    layout_receiver.setVisibility(View.VISIBLE);
                } else {
                    // Caller
                    if (Receiver.getFullname() != null) {
                        tv_infor.setText("Đang gọi cho " + Receiver.getFullname());
                    } else {
                        tv_infor.setText("Đang gọi cho Unknown");
                    }
                    if (Caller.getAvatar() != null) {
                        Picasso.get().load(Receiver.getAvatar()).into(circle_avatar);
                    }
                    layout_caller.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        makeCall();
        // Layout Caller
        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(Call.this, CallRoomService.class);
                stopService(serviceIntent);
                mCallDatabase.child(idRecicer).child(id).child("calling").setValue(false);
                mCallDatabase.child(idRecicer).child(id).child("end").setValue(true);
            }
        });
        // layout receiver
        btnaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isJoined = true;
                mCallDatabase.child(idRecicer).child(id).child("acceptCall").setValue(true);
            }
        });
        btnrefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallDatabase.child(idRecicer).child(id).child("calling").setValue(false);
                mCallDatabase.child(idRecicer).child(id).child("refuseCall").setValue(true);
                Intent serviceIntent = new Intent(Call.this, CallRoomService.class);
                stopService(serviceIntent);
            }
        });
        // layout joined
        btnleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent serviceIntent = new Intent(Call.this, CallRoomService.class);
//                stopService(serviceIntent);
                mCallDatabase.child(idRecicer).child(id).child("calling").setValue(false);
                mCallDatabase.child(idRecicer).child(id).child("end").setValue(true);
            }
        });
        img_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ismutex) {
                    ismutex = true;
                    agoraEngine.muteLocalAudioStream(true);
                    img_voice.setBackground(getApplicationContext().getDrawable(R.drawable.icon_nonevoice));
                } else {
                    ismutex = false;
                    agoraEngine.muteLocalAudioStream(false);
                    img_voice.setBackground(getApplicationContext().getDrawable(R.drawable.icon_voice));
                }
            }
        });
        mCallDatabase.child(idRecicer).child(id).child("acceptCall").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue().equals(true)) {
                    layout_caller.setVisibility(View.GONE);
                    layout_receiver.setVisibility(View.GONE);
                    layout_joined.setVisibility(View.VISIBLE);
                    setupVoiceSDKEngine();
                    joinChannel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mCallDatabase.child(idRecicer).child(id).child("refuseCall").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mCallDatabase.child(idRecicer).child(id).child("refuseCall").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue().equals(true)) {
                    if (FirebaseAuth.getInstance().getUid().equals(idCaller)) {
                        if(countToast < 1)
                        {
                            Toast.makeText(Call.this, "Người dùng đã từ chối", Toast.LENGTH_SHORT).show();
                            countToast++;
                        }
                    }
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        mCallDatabase.child(idRecicer).child(id).child("end").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && snapshot.getValue().equals(true)) {
//                    if(isJoined)
//                    {
//                        Toast.makeText(Call.this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
//                        agoraEngine.leaveChannel();
//                    }
//                    finish();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        // Khởi tạo ValueEventListener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xử lý dữ liệu khi có thay đổi
                if (snapshot.exists() && snapshot.getValue().equals(true)) {
                    if (isJoined) {
                        Toast.makeText(Call.this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
                        agoraEngine.leaveChannel();
                        mCallDatabase.child(idRecicer).child(id).child("end").removeEventListener(valueEventListener);
                    }
                    finish();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        };
        // Đăng ký sự kiện lắng nghe
        mCallDatabase.child(idRecicer).child(id).child("end").addValueEventListener(valueEventListener);
        mUserDatabase.child(idCaller).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean online = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if(!online)
                {
                    if(isJoined)
                    {
                        agoraEngine.leaveChannel();
                        Toast.makeText(Call.this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
                    }else{
                        onDestroy();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mUserDatabase.child(idRecicer).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean online = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if(!online)
                {
                    if(isJoined)
                    {
                        agoraEngine.leaveChannel();
                        Toast.makeText(Call.this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
                    }else{
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    int countToast = 0;

    ValueEventListener valueEventListener;

    private boolean ismutex = false;
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO
            };

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote user joining the channel.
        public void onUserJoined(int uid, int elapsed) {
            runOnUiThread(() -> Toast.makeText(Call.this, "", Toast.LENGTH_SHORT));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            // Successfully joined a channel
            isJoined = true;
//            showMessage("Đã tham gia vào kênh " + channel);
            runOnUiThread(() ->
                    Toast.makeText(Call.this, "", Toast.LENGTH_SHORT));
//            tv_infor.setText("Waiting for a remote user to join")
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            // Listen for remote users leaving the channel
//            updateChildren();
//            showMessage("Cuộc gọi đã kết thúc " + uid + " " + reason);
//            finish();
//            runOnUiThread(() -> finish());
//            if (isJoined) runOnUiThread(()->tv_infor.setText("Waiting for a remote user to join"));
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            // Listen for the local user leaving the channel
            runOnUiThread(() -> Toast.makeText(Call.this, "", Toast.LENGTH_SHORT));
            isJoined = false;
            finish();
        }
    };

    private void updateChildren() {
        mCallDatabase.child(idRecicer).child(id).child("calling").setValue(false);
        mCallDatabase.child(idRecicer).child(id).child("acceptCall").setValue(false);
        mCallDatabase.child(idRecicer).child(id).child("refuseCall").setValue(false);
        mCallDatabase.child(idRecicer).child(id).child("end").setValue(false);
    }


    private void joinChannel() {
        ChannelMediaOptions options = new ChannelMediaOptions();
        options.autoSubscribeAudio = true;
        // Set both clients as the BROADCASTER.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
        // Set the channel profile as BROADCASTING.
        options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channelName, uid, options);
        startCountTimeCall();
    }
    private DatabaseReference mStatusDatabase = FirebaseDatabase.getInstance().getReference("Status");

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
        mStatusDatabase.child(idCaller).setValue(false);
        mStatusDatabase.child(idRecicer).setValue(false);
        HashMap hashMap = new HashMap();
        hashMap.put("id", id);
        hashMap.put("calling", false);
        hashMap.put("idCaller", idCaller);
        hashMap.put("callingVideo", false);
        hashMap.put("acceptCall", false);
        hashMap.put("refuseCall", false);
        hashMap.put("end", false);
        mCallDatabase.child(idRecicer).child(id).updateChildren(hashMap);

        Intent serviceIntent = new Intent(Call.this, CallRoomService.class);
        stopService(serviceIntent);
        // Destroy the engine in a sub-thread to avoid congestion
        agoraEngine.leaveChannel();
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
        finish();
    }


}
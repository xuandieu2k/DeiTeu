package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rtc2.ChannelMediaOptions;



import com.example.deiteu.R;
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

import java.util.HashMap;


public class VideoCall extends AppCompatActivity {
    // Fill the App ID of your project generated on Agora Console.
    private String appId = "";
    // Fill the channel name.
    private String channelName = "";
    // Fill the temp token generated on Agora Console.
    private String token = "";
    // An integer that identifies the local user.
    private String appCertificate = "";
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;
    private TextView tv_infor;
    CircleImageView circle_avatar;
    private CircleImageView btnaccept, btnrefuse, img_voice, btnend, btnleave,img_swipecamera,img_onoffcamera;
    String id, idCaller, idRecicer;
    LinearLayout layout_receiver, layout_caller, layout_joined,local_video_view_container,remote_video_view_container;
    public DatabaseReference mCallDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("CallRoom");
    public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
    Users Caller = new Users();
    Users Receiver = new Users();
    private CountDownTimer callTimer;
    private long callStartTime;
    int countToast = 0;

    ValueEventListener valueEventListener;

    private boolean ismutex,isonoffcamera = false;
    private GenerateTokenAgora generateTokenAgora;
    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo();
        } catch (Exception e) {
            showMessage(e.toString());
        }
    }
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
//            showMessage("Remote user joined " + uid);

            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
//            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
//            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> finish());
//            Toast.makeText(VideoCall.this, "", Toast.LENGTH_SHORT);
//            remoteSurfaceView.setVisibility(View.GONE)
        }
    };
    private void setupRemoteVideo(int uid) {
        LinearLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
//        remoteSurfaceView.setZOrderMediaOverlay(false);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        // Display RemoteSurfaceView.
//        agoraEngine.adjustPlaybackSignalVolume(100);
        remoteSurfaceView.setVisibility(View.VISIBLE);
        agoraEngine.setCameraAutoFocusFaceModeEnabled(true);
    }
    private void setupLocalVideo() {
        LinearLayout container = findViewById(R.id.local_video_view_container);
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = new SurfaceView(getBaseContext());
        localSurfaceView.setZOrderOnTop(true);
        container.addView(localSurfaceView);
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
        agoraEngine.setEnableSpeakerphone(true);
//        agoraEngine.adjustPlaybackSignalVolume(100);
        agoraEngine.setCameraAutoFocusFaceModeEnabled(true);
    }
    public void joinChannel(View view) {
        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();
            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            // Display LocalSurfaceView.
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            // Start local preview.
            agoraEngine.startPreview();
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine.joinChannel(token, channelName, uid, options);
        } else {
//            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
    }
    public void leaveChannel(View view) {
        if (!isJoined) {
//            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
//            showMessage("You left the channel");
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        generateTokenAgora = new GenerateTokenAgora();
        btnaccept = findViewById(R.id.btnaccept);
        btnleave = findViewById(R.id.btnleave);
        appId = getApplicationContext().getString(R.string.appId);
        appCertificate = getApplicationContext().getString(R.string.appCertificate);
        //
        // Set up access to the UI elements
        layout_caller = findViewById(R.id.layout_caller);
        btnend = findViewById(R.id.btnend);
        //
        layout_joined = findViewById(R.id.layout_joined);
        btnleave = findViewById(R.id.btnleave);
        img_voice = findViewById(R.id.img_voice);
        img_swipecamera= findViewById(R.id.img_swipecamera);
        img_onoffcamera   = findViewById(R.id.img_onoffcamera);
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
        //
        channelName = idCaller+idRecicer;
        token = generateTokenAgora.GenerateTokenVideoCall(appId,appCertificate,channelName,uid);
        layout_joined.setVisibility(View.GONE);
        layout_receiver.setVisibility(View.GONE);
        layout_caller.setVisibility(View.GONE);
        remote_video_view_container = findViewById(R.id.remote_video_view_container);
        local_video_view_container = findViewById(R.id.local_video_view_container);
        local_video_view_container.setVisibility(View.GONE);
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
                        tv_infor.setText("Cuộc gọi video từ " + Caller.getFullname());
                    } else {
                        tv_infor.setText("Cuộc gọi video từ Unknown");
                    }
                    if (Caller.getAvatar() != null) {
                        Picasso.get().load(Caller.getAvatar()).into(circle_avatar);
                    }
                    layout_receiver.setVisibility(View.VISIBLE);
                } else {
                    // Caller
                    if (Receiver.getFullname() != null) {
                        tv_infor.setText("Đang gọi video cho " + Receiver.getFullname());
                    } else {
                        tv_infor.setText("Đang gọi video cho Unknown");
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
        btnend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent serviceIntent = new Intent(VideoCall.this, CallRoomService.class);
                stopService(serviceIntent);
                mCallDatabase.child(idRecicer).child(id).child("callingVideo").setValue(false);
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
                mCallDatabase.child(idRecicer).child(id).child("callingVideo").setValue(false);
                mCallDatabase.child(idRecicer).child(id).child("refuseCall").setValue(true);
                Intent serviceIntent = new Intent(VideoCall.this, CallRoomService.class);
                stopService(serviceIntent);
            }
        });
        // layout joined
        btnleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent serviceIntent = new Intent(Call.this, CallRoomService.class);
//                stopService(serviceIntent);
                mCallDatabase.child(idRecicer).child(id).child("callingVideo").setValue(false);
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
        img_swipecamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agoraEngine.switchCamera();
            }
        });
        img_onoffcamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isonoffcamera) {
                    isonoffcamera = true;
                    agoraEngine.enableLocalVideo(false);
                    img_onoffcamera.setBackground(getApplicationContext().getDrawable(R.drawable.icon_nonecamera));
                } else {
                    isonoffcamera = false;
                    agoraEngine.enableLocalVideo(true);
                    img_onoffcamera.setBackground(getApplicationContext().getDrawable(R.drawable.icon_camera));
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
                    setupVideoSDKEngine();
                    View currentView = VideoCall.this.getCurrentFocus();
                    local_video_view_container.setVisibility(View.VISIBLE);
                    joinChannel(currentView);
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
                            Toast.makeText(VideoCall.this, "Người dùng đã từ chối", Toast.LENGTH_SHORT).show();
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
        // Khởi tạo ValueEventListener
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xử lý dữ liệu khi có thay đổi
                if (snapshot.exists() && snapshot.getValue().equals(true)) {
                    if (isJoined) {
                        mCallDatabase.child(idRecicer).child(id).child("end").removeEventListener(valueEventListener);
                        Toast.makeText(VideoCall.this, "Cuộc gọi đã kết thúc", Toast.LENGTH_SHORT).show();
                        agoraEngine.leaveChannel();
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
        //
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }
        setupVideoSDKEngine();
    }
    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };

    private boolean checkSelfPermission()
    {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }
    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }
    private DatabaseReference mStatusDatabase = FirebaseDatabase.getInstance().getReference("Status");
    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();
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
        Intent serviceIntent = new Intent(VideoCall.this, CallRoomService.class);
        stopService(serviceIntent);
        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
        finish();
    }



}
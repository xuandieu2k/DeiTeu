package com.example.deiteu.model;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.deiteu.activity.Call;
import com.example.deiteu.activity.DetailsMessage;
import com.example.deiteu.activity.HelloUser;
import com.example.deiteu.activity.Home2;
import com.example.deiteu.activity.LoginFacebook;
import com.example.deiteu.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FirebaseHandler extends Application {
    private static final String TAG = "FirebaseHandler";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userRef, mOnlineRef, mUserdatabase;
    private FirebaseAuth mAuth;

    private FirebaseUser mUser;
    private DatabaseReference lastOnlineRef;
    private DatabaseReference mCallDatabase;
    List<Users> usersList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());

        // Enable Firebase Database persistence
        FirebaseDatabase.getInstance()
                .setPersistenceEnabled(true);

        // Create database reference for ".info/connected"
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");

        // Create database reference for user's online status
        firebaseDatabase = FirebaseDatabase.getInstance();
        mCallDatabase = FirebaseDatabase.getInstance().getReference("CallRoom");

//        userRef = firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mOnlineRef = firebaseDatabase.getReference("Users");
        lastOnlineRef = firebaseDatabase.getReference("Users");
        mOnlineRef.keepSynced(false);
        mOnlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Users us = dataSnapshot.getValue(Users.class);
                    usersList.add(us);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Listen for changes in connection state
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                if (connected) {
                    if (mUser != null) {
                        mOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("online");
                        lastOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("timelastonline");
//                      When this device disconnects, remove it
                        mOnlineRef.onDisconnect().setValue(false);
//                      When I disconnect, update the last time I was seen online
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                        // Add this device to my connections list
                        // this value could contain info about the device or a timestamp too
                        mOnlineRef.setValue(true);
                        lastOnlineRef.setValue(ServerValue.TIMESTAMP);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "Listener was cancelled");
//                mOnlineRef.onDisconnect().setValue(Boolean.FALSE);
                mOnlineRef.onDisconnect().setValue(false);
                lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
            }
        });
    }
    private class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            activity.overridePendingTransition(0, 0);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

        // Implement other methods of ActivityLifecycleCallbacks as needed
    }


    public void Logout(String idUser) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mOnlineRef = firebaseDatabase.getReference("Users");
        lastOnlineRef = firebaseDatabase.getReference("Users");

        mOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser).child("online");
        lastOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser).child("timelastonline");
        mOnlineRef.setValue(false);
        lastOnlineRef.setValue(ServerValue.TIMESTAMP);
    }
    public void Login(String idUser) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mOnlineRef = firebaseDatabase.getReference("Users");
        lastOnlineRef = firebaseDatabase.getReference("Users");

        mOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser).child("online");
        lastOnlineRef = FirebaseDatabase.getInstance().getReference().child("Users").child(idUser).child("timelastonline");
        mOnlineRef.setValue(true);
        lastOnlineRef.setValue(ServerValue.TIMESTAMP);
    }

    boolean check = false;
    public void gotoActivity(String uID,Context context)
    {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        mDatabase.child(uID).keepSynced(false);
        mDatabase.child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Users users = snapshot.getValue(Users.class);
                    assert users != null;
                    if (users.getFullname() != null || (users.getGender() != null && users.getGender() != "")) {
                        check = true;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(!check)
        {
            Intent intent = new Intent(context, HelloUser.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

}
// class NetworkChangeReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(final Context context, final Intent intent) {
//
//        final ConnectivityManager connMgr = (ConnectivityManager) context
//                .getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
//        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
//
//        if (isConnected) {
//
//        }else{
//            // Create database reference for ".info/connected"
//            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
//
//            // Create database reference for user's online status
//            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
////        userRef = firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));
//            DatabaseReference mOnlineRef = firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("online");
//            final DatabaseReference lastOnlineRef = firebaseDatabase.getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("timelastonline");
//
//            // Listen for changes in connection state
//            connectedRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    boolean connected = snapshot.getValue(Boolean.class);
//                    if (connected) {
//                        // When this device disconnects, remove it
//                        mOnlineRef.onDisconnect().setValue(Boolean.FALSE);
//                        // When I disconnect, update the last time I was seen online
//                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
//                        // Add this device to my connections list
//                        // this value could contain info about the device or a timestamp too
//                        mOnlineRef.setValue(Boolean.TRUE);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Log.w(TAG, "Listener was cancelled");
//                    mOnlineRef.onDisconnect().setValue(Boolean.FALSE);
//                }
//            });
//        }
//    }
//}









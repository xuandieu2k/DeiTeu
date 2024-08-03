package com.example.deiteu.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.deiteu.activity.Call;
import com.example.deiteu.activity.VideoCall;
import com.example.deiteu.model.CallRoom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CallRoomService extends Service {

    private DatabaseReference mDatabaseReference;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mStatusDatabase = FirebaseDatabase.getInstance().getReference("Status");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String idU = FirebaseAuth.getInstance().getUid();
        // Khởi tạo Firebase Database
        assert idU != null;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("CallRoom").child(idU);
        // Đăng ký ValueEventListener để lắng nghe sự kiện thay đổi trên CallRoom
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CallRoom callroom = snapshot.getValue(CallRoom.class);
                checkStatus = false;
                mStatusDatabase.child(idU).keepSynced(true);
                mStatusDatabase.child(idU).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            checkStatus= Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(!checkStatus)
                {
                    if(callroom.isCalling())
                    {
                        mStatusDatabase.child(idU).keepSynced(true);
                        mStatusDatabase.child(idU).setValue(true);
                        Intent intent = new Intent(getApplicationContext(), Call.class);
                        intent.putExtra("id", callroom.getId());
                        intent.putExtra("idCaller", callroom.getIdCaller());
                        intent.putExtra("idRecicer", idU);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        if(callroom.isCallingVideo())
                        {
                            mStatusDatabase.child(idU).keepSynced(true);
                            mStatusDatabase.child(idU).setValue(true);
                            Intent intent = new Intent(getApplicationContext(), VideoCall.class);
                            intent.putExtra("id", callroom.getId());
                            intent.putExtra("idCaller", callroom.getIdCaller());
                            intent.putExtra("idRecicer", idU);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                CallRoom callroom = snapshot.getValue(CallRoom.class);
                checkStatus = false;
                mStatusDatabase.child(idU).keepSynced(true);
                mStatusDatabase.child(idU).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            checkStatus =  Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if(!checkStatus)
                {
                    if(callroom.isCalling())
                    {
                        mStatusDatabase.child(idU).keepSynced(true);
                        mStatusDatabase.child(idU).setValue(true);
                        Intent intent = new Intent(getApplicationContext(), Call.class);
                        intent.putExtra("id", callroom.getId());
                        intent.putExtra("idCaller", callroom.getIdCaller());
                        intent.putExtra("idRecicer", idU);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        if(callroom.isCallingVideo())
                        {
                            mStatusDatabase.child(idU).keepSynced(true);
                            mStatusDatabase.child(idU).setValue(true);
                            Intent intent = new Intent(getApplicationContext(), VideoCall.class);
                            intent.putExtra("id", callroom.getId());
                            intent.putExtra("idCaller", callroom.getIdCaller());
                            intent.putExtra("idRecicer", idU);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                }
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

        return START_STICKY;
    }
    boolean checkStatus = false;

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký ValueEventListener
//        if (mDatabaseReference != null && mValueEventListener != null) {
//            mDatabaseReference.removeEventListener(mValueEventListener);
//        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


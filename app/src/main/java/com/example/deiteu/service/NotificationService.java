package com.example.deiteu.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.deiteu.R;
import com.example.deiteu.activity.Details_Post;
import com.example.deiteu.model.Notification;
import com.example.deiteu.model.NotificationHelper;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class NotificationService extends Service {

    private DatabaseReference mDatabaseReference;
    private NotificationHelper notificationHelperl;
    private List<Users> usersList = new ArrayList<>();
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    int count = 0;
    List<Notification> notificationList = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationHelperl = new NotificationHelper(getApplicationContext());
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseReference.keepSynced(false);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users us = dataSnapshot.getValue(Users.class);
                    usersList.add(us);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        String idU = FirebaseAuth.getInstance().getUid();
        // Khởi tạo Firebase Database
        assert idU != null;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notification").child(idU);
        mDatabaseReference.keepSynced(false);
        ValueEventListener mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count++;
                if (count > 1) {
                    if (snapshot.exists()) {
                        int countt = notificationList.size();
                        notificationList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Notification notification = dataSnapshot.getValue(Notification.class);
                            notificationList.add(notification);
                        }
                        Collections.sort(notificationList, new NotificationComparator());
                        Notification notification = notificationList.get(notificationList.size() - 1);
                        String name = "Unknown";
                        for (Users us : usersList) {
                            assert notification != null;
                            if (us.getId().equals(notification.getIdUser())) {
                                name = us.getFullname();
                            }
                        }
                        if (notificationList.size() > countt && !notification.isReaded()) {
                            notificationHelperl.createNotification("Thông báo mới", name + " " + notification.getContent().toLowerCase(),
                                    notification.getIdPost(), notification.getIdUser(), Details_Post.class, 0);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        mDatabaseReference.addValueEventListener(mValueEventListener);
        return START_STICKY;
    }

    private static class NotificationComparator implements Comparator<Notification> {

        @Override
        public int compare(Notification n1, Notification n2) {
            return Long.compare(n1.getTimeCreated(), n2.getTimeCreated());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}

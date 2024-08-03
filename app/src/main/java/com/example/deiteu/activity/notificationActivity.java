package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.deiteu.R;
import com.example.deiteu.adapter.ListNotificationAdapter;
import com.example.deiteu.model.Notification;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class notificationActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView tv_textnotification,tv_markread;
    ImageButton btnimg_back;
    ListNotificationAdapter listNotificationAdapter;
    private DatabaseReference mNotificationDatabaseReference;
    LinearLayout layout_no_notification;
    List<Users> usersList = new ArrayList<>();
    private DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mNotificationDatabaseReference = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Notification");
        listNotificationAdapter = new ListNotificationAdapter(getApplicationContext());
        recyclerView = findViewById(R.id.rcylelistuser);
        recyclerView.setAdapter(listNotificationAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setNestedScrollingEnabled(false); // Mượt
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        tv_textnotification = findViewById(R.id.tv_textnotification);
        tv_markread = findViewById(R.id.tv_markread);
        btnimg_back = findViewById(R.id.btnimg_back);
        btnimg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // hide recycle and textview
        recyclerView.setVisibility(View.GONE);
        tv_markread.setVisibility(View.GONE);
        layout_no_notification = findViewById(R.id.layout_no_notification);
        layout_no_notification.setVisibility(View.VISIBLE);
        String idUserCurrent = FirebaseAuth.getInstance().getUid();
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Users us = dataSnapshot.getValue(Users.class);
                        usersList.add(us);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tv_markread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNotificationDatabaseReference.child(idUserCurrent).keepSynced(true);
                mNotificationDatabaseReference.child(idUserCurrent).orderByChild("timeCreated").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            {
                                Notification notification = dataSnapshot.getValue(Notification.class);
                                mNotificationDatabaseReference.child(idUserCurrent).child(notification.getId())
                                        .child("readed").setValue(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        mNotificationDatabaseReference.child(idUserCurrent).orderByChild("timeCreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listNotificationAdapter.clear();
                if(snapshot.exists())
                {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        for (Users users : usersList)
                        {
                            if(users.getId().equals(notification.getIdUser()))
                            {
                                notification.setUsers(users);
                                listNotificationAdapter.add(notification);
                                break;
                            }
                        }
                    }
                    listNotificationAdapter.revese();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Objects.requireNonNull(recyclerView.getAdapter()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (recyclerView.getAdapter().getItemCount() > 0) {
                    layout_no_notification.setVisibility(View.GONE);
                    tv_markread.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    layout_no_notification.setVisibility(View.VISIBLE);
                    tv_markread.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });


    }
}
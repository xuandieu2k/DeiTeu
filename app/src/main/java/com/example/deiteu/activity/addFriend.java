package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.deiteu.R;
import com.example.deiteu.adapter.ListAddFriendAdapter;
import com.example.deiteu.adapter.ListUserAdapter;
import com.example.deiteu.model.Message;
import com.example.deiteu.model.SearchUtils;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class addFriend extends AppCompatActivity {
    ImageButton btnimg_back;
    RecyclerView recyclerView;
    ListAddFriendAdapter listAddFriendAdapter;
    EditText edtsearch;
    TextView tv_message;
    private DatabaseReference mDatabaseReference;
    private SearchUtils searchUtils = new SearchUtils();
    private List<Users> listAllUser = new ArrayList<>();
    RelativeLayout layout_notfound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        btnimg_back = findViewById(R.id.btnimg_back);
        edtsearch = findViewById(R.id.edtsearch);
        recyclerView = findViewById(R.id.rcylelistuser);
        layout_notfound = findViewById(R.id.layout_notfound);
        tv_message = findViewById(R.id.tv_messagess);
        tv_message.setText("Tìm kiếm người thương");
        recyclerView.setVisibility(View.GONE);
        layout_notfound.setVisibility(View.VISIBLE);
        mDatabaseReference = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        btnimg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listAddFriendAdapter = new ListAddFriendAdapter(getApplicationContext());
        recyclerView = findViewById(R.id.rcylelistuser);
        recyclerView.setAdapter(listAddFriendAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setNestedScrollingEnabled(false); // Mượt
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        getUserALlFirst();
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = edtsearch.getText().toString().trim();
                //
                if (searchString.equals("")) {
                    listAddFriendAdapter.clear();
                    recyclerView.setVisibility(View.GONE);
                    layout_notfound.setVisibility(View.VISIBLE);
                    tv_message.setText("Tìm kiếm người thương");
                } else {
                    listAddFriendAdapter.clear();
                    List<Users> list = searchUtils.searchUsers(listAllUser, searchString);
                    listAddFriendAdapter.addAll(list);
                    if(listAddFriendAdapter.getItemCount()>0)
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        layout_notfound.setVisibility(View.GONE);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        layout_notfound.setVisibility(View.VISIBLE);
                        tv_message.setText("Không tìm thấy người dùng phù hợp");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
    public void getUserALl() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = FirebaseAuth.getInstance().getUid();
                listAddFriendAdapter.clear();
                listAllUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();
                    assert uid != null;
                    if (!uid.equals(id)) {
                        if (dataSnapshot.exists()) {
                            Users us = dataSnapshot.getValue(Users.class);
                            listAllUser.add(us);
                            listAddFriendAdapter.add(us);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void getUserALlFirst() {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = FirebaseAuth.getInstance().getUid();
                listAllUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();
                    if (!uid.equals(id)) {
                        if (dataSnapshot.exists()) {
                            Users us = dataSnapshot.getValue(Users.class);
                            listAllUser.add(us);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
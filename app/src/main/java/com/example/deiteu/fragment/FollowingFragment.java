package com.example.deiteu.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.adapter.ListUserSearchAdapter;
import com.example.deiteu.model.Follows;
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

public class FollowingFragment extends Fragment {
    RecyclerView recycleview;
    EditText edtsearch;
    TextView tv_message;
    RelativeLayout layout_notfound;
    ListUserSearchAdapter listUserSearchAdapter;
    public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");
    public DatabaseReference mFollowDatabase = FirebaseDatabase.getInstance().getReference("Follows");
    private SearchUtils searchUtils = new SearchUtils();
    public FollowingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycleview = view.findViewById(R.id.recyclelistU);
        edtsearch = view.findViewById(R.id.edtsearch);
        //
        layout_notfound = view.findViewById(R.id.layout_notfound);
        tv_message = view.findViewById(R.id.tv_messagess);
        layout_notfound.setVisibility(View.GONE);
        //
        listUserSearchAdapter = new ListUserSearchAdapter(getContext(),0);
        recycleview.setAdapter(listUserSearchAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recycleview.setLayoutManager(layoutManager);
        recycleview.setHasFixedSize(true);
        recycleview.setItemViewCacheSize(20);
        recycleview.setDrawingCacheEnabled(true);
        recycleview.setNestedScrollingEnabled(false); // Mượt
        recycleview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        getMessagefromFirebase();
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                // Xử lý sự kiện khi dữ liệu của Adapter thay đổi
                if(listUserSearchAdapter.getItemCount()>0)
                {
                    recycleview.setVisibility(View.VISIBLE);
                    layout_notfound.setVisibility(View.GONE);
                }else{
                    recycleview.setVisibility(View.GONE);
                    layout_notfound.setVisibility(View.VISIBLE);
                    tv_message.setText("Bạn chưa theo dõi người dùng nào");
                }
            }
        };
        listUserSearchAdapter.registerAdapterDataObserver(observer);
        edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = edtsearch.getText().toString().trim();
                //
                if (searchString.equals("")) {
                    getMessagefromFirebase();
                } else {
                    List<Users> list = searchUtils.searchUsers(listAllUser,searchString);
                    listUserSearchAdapter.clear();
                    listUserSearchAdapter.addAll(list);
                    //
                    if(listUserSearchAdapter.getItemCount()>0)
                    {
                        recycleview.setVisibility(View.VISIBLE);
                        layout_notfound.setVisibility(View.GONE);
                    }else{
                        recycleview.setVisibility(View.GONE);
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
    private List<Users> listAllUser = new ArrayList<>();

    private void getMessagefromFirebase() {
        String idUser = FirebaseAuth.getInstance().getUid();
        mFollowDatabase.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUserSearchAdapter.clear();
                listAllUser.clear();
                if(snapshot.exists())
                {
                    for (DataSnapshot data : snapshot.getChildren())
                    {
                        for (DataSnapshot datachild: data.getChildren())
                        {
                            Follows follows = datachild.getValue(Follows.class);
                            assert follows != null;
                            if(follows.getIdFollower().equals(idUser))
                            {
                                String id = data.getKey();
                                mUserDatabase.child(id).keepSynced(false);
                                mUserDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Users us = snapshot.getValue(Users.class);
                                        listUserSearchAdapter.add(us);
                                        listAllUser.add(us);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
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


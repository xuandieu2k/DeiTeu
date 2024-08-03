package com.example.deiteu.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.deiteu.R;
import com.example.deiteu.activity.addFriend;
import com.example.deiteu.activity.contactFriend;
import com.example.deiteu.adapter.ListUserAdapter;
import com.example.deiteu.model.FirebaseHandler;
import com.example.deiteu.model.Message;
import com.example.deiteu.model.SearchUtils;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private DatabaseReference mDatabaseReference;
    private ListUserAdapter listUserAdapter;
    private RecyclerView recyclerView;
    ImageButton btnimg_addfriend, btnimg_listfriend;

    private DatabaseReference mMessageRef;

    private EditText edt_search;
    TextView tv_message;
    RelativeLayout layout_notfound;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    private List<Users> usersList;
    FirebaseHandler firebaseHandler = new FirebaseHandler();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnimg_addfriend = view.findViewById(R.id.btnimg_addfriend);
        btnimg_listfriend = view.findViewById(R.id.btnimg_listfriend);
        //
        layout_notfound = view.findViewById(R.id.layout_notfound);
        tv_message = view.findViewById(R.id.tv_messagess);
        layout_notfound.setVisibility(View.GONE);
        //
        btnimg_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), addFriend.class);
                startActivity(it);
            }
        });
        btnimg_listfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), contactFriend.class);
                startActivity(it);
            }
        });
        listUserAdapter = new ListUserAdapter(getContext());
        recyclerView = view.findViewById(R.id.rcylelistuser);
        recyclerView.setAdapter(listUserAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setNestedScrollingEnabled(false); // Mượt
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Users");
        mMessageRef = FirebaseDatabase.getInstance().getReference("Message");
        getMessagefromFirebase();
//        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                // Xử lý sự kiện khi dữ liệu của Adapter thay đổi
//                if(listUserAdapter.getItemCount()>0)
//                {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    layout_notfound.setVisibility(View.GONE);
//                }else{
//                    recyclerView.setVisibility(View.GONE);
//                    layout_notfound.setVisibility(View.VISIBLE);
//                    tv_message.setText("Sao cô đơn thế? Kiếm người mà nhắn tin đi chứ");
//                }
//            }
//        };
        edt_search = view.findViewById(R.id.edtsearch);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchString = edt_search.getText().toString().trim();
                //
                if (searchString.equals("")) {
                    getMessagefromFirebase();
                } else {
                    listUserAdapter.clear();
                    List<Users> list = searchUtils.searchUsers(listAllUser, searchString);
                    listUserAdapter.addAll(list);
                    if(listUserAdapter.getItemCount()>0)
                    {
                        recyclerView.setVisibility(View.VISIBLE);
                        layout_notfound.setVisibility(View.GONE);
                    }else{
                        recyclerView.setVisibility(View.GONE);
                        layout_notfound.setVisibility(View.VISIBLE);
                        tv_message.setText("Không tìm thấy người dùng phù hợp");
                    }
//                    listUserAdapter.registerAdapterDataObserver(observer);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private SearchUtils searchUtils = new SearchUtils();
    private List<Users> listAllUser = new ArrayList<>();

    public void getMessagefromFirebase() {
        mDatabaseReference.keepSynced(false);
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = FirebaseAuth.getInstance().getUid();
                listUserAdapter.clear();
                listAllUser.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();
                    if (!uid.equals(id)) {
                        if (dataSnapshot.exists()) {
                            Users us = dataSnapshot.getValue(Users.class);
                            mMessageRef.child(id + uid).orderByChild("timeSend").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                    if (snapshot1.exists()) {
                                        Message mess = new Message();
                                        for (DataSnapshot datasnap : snapshot1.getChildren()) {
                                            mess = datasnap.getValue(Message.class);
                                            assert us != null;
                                            us.setFinalMessage(mess);
                                            if(listUserAdapter.getListUser().contains(us))
                                            {
                                                listUserAdapter.replaceUser(listUserAdapter.getUsersList(),us);
//                                                listUserAdapter.replaceUser(listAllUser,us);
                                            }else{
                                                listUserAdapter.add(us);
                                                listAllUser.add(us);
                                            }
                                        }
                                    }
                                    listUserAdapter.sortTimesend();
                                    listUserAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
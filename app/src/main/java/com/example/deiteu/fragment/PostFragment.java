package com.example.deiteu.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.activity.create_post;
import com.example.deiteu.activity.notificationActivity;
import com.example.deiteu.adapter.ListUserAdapter;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.adapter.ListPostAdapter;
import com.example.deiteu.model.Notification;
import com.example.deiteu.model.Post;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recycleview;

    private ImageView btn_addPost;

    private ProgressBar proges_loadpage;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageButton button_notification;

    private ListPostAdapter listPostAdapter;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("Users");
    private DatabaseReference databaseReference;
    private DatabaseReference mCommentDatabase = FirebaseDatabase.getInstance().getReference("Comments");

    private DatabaseReference mInteractDatabase = FirebaseDatabase.getInstance().getReference("Interact");

    private Users us = new Users();

    private boolean isScrolling = false;
    private SharedPreferences sharedPreferences;
    private TextView tv_count;
    private DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance().getReference("Notification");
    private DatabaseReference mBlacklistDatabase = FirebaseDatabase.getInstance().getReference("Blacklist");


    public PostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostFragment newInstance(String param1, String param2) {
        PostFragment fragment = new PostFragment();
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
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    int count = 0;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        button_notification = view.findViewById(R.id.button_notification);
        listPostAdapter = new ListPostAdapter(getContext());
        recycleview = view.findViewById(R.id.recycleview);
        recycleview.setAdapter(listPostAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recycleview.setLayoutManager(layoutManager);
        recycleview.setHasFixedSize(true);
        recycleview.setItemViewCacheSize(20);
        recycleview.setDrawingCacheEnabled(true);
        recycleview.setNestedScrollingEnabled(false); // Mượt
        recycleview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycleview.setVisibility(View.GONE);
//        layoutManager.setReverseLayout(true);
//        recycleview.scrollToPosition(listPostAdapter.getItemCount() -1);
        tv_count = view.findViewById(R.id.tv_count);
        tv_count.setText("");
        btn_addPost = view.findViewById(R.id.btn_addPost);
        btn_addPost.setVisibility(View.GONE);
        //
        String idUserCurrent = FirebaseAuth.getInstance().getUid();
        mNotificatonDatabase.child(idUserCurrent).orderByChild("timeCreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        if (!notification.isReaded()) {
                            count++;
                        }
                    }
                    if (count > 0) {
                        if (count > 99) {
                            tv_count.setText("99+");
                        } else {
                            tv_count.setText("" + count);
                        }
                    } else {
                        tv_count.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        proges_loadpage = view.findViewById(R.id.proges_loadpage);
        proges_loadpage.setVisibility(View.GONE);
        recycleview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // Khi không scroll, hiển thị nút
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            btn_addPost.setVisibility(View.VISIBLE);
//                        }
//                    },4000);
                    btn_addPost.setVisibility(View.VISIBLE);
                } else {
                    // Khi scroll, ẩn nút
                    btn_addPost.setVisibility(View.GONE);
                    swipeRefreshLayout.setEnabled(false);
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(-1)) {
                    // RecyclerView ở vị trí trên cùng của danh sách, cho phép SwipeRefreshLayout thực hiện tải lại dữ liệu
                    swipeRefreshLayout.setEnabled(true);
                } else {
                }

                if (!recyclerView.canScrollVertically(1)) {
                    // Load thêm dữ liệu ở đây
                    // Sau khi load xong, thêm vào danh sách hiện có bằng cách gọi listPostAdapter.notifyDataSetChanged();
                    if (listPostAdapter.getItemCount() > 0) {
                        Toast.makeText(getContext(), "Không có tin nào mới.", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefreshLayout.setEnabled(false);
                }
            }

        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Kiểm tra xem RecyclerView có ở vị trí trên cùng của danh sách hay không
                if (!recycleview.canScrollVertically(-1)) {
                    // Nếu không ở vị trí trên cùng, thực hiện tải lại dữ liệu
                    swipeRefreshLayout.setRefreshing(true);
                    loadData();
                } else {
                    // Nếu đang ở vị trí trên cùng, chỉ hiển thị thông báo
//                    Toast.makeText(getContext(), "Bạn đang ở đáy trang.", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        btn_addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), create_post.class);
                startActivity(it);
            }
        });
        button_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getContext(), notificationActivity.class);
                startActivity(it);
            }
        });

        //


        // Đăng ký một AdapterDataObserver
        Objects.requireNonNull(recycleview.getAdapter()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (recycleview.getAdapter().getItemCount() > 0) {
                    proges_loadpage.setVisibility(View.GONE);
                    recycleview.setVisibility(View.VISIBLE);
                    btn_addPost.setVisibility(View.VISIBLE);
                } else {
                    proges_loadpage.setVisibility(View.VISIBLE);
                    btn_addPost.setVisibility(View.GONE);
                }
            }
        });
        loadData();

    }

    List<String> idStringBlackListReceiver = new ArrayList<>();
    List<String> idStringBlackListSender = new ArrayList<>();

    public void getListU(String idUserPost) {
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
                mBlacklistDatabase.child(idUserPost).addValueEventListener(new ValueEventListener() { // Receiver
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Blacklist follows = data.getValue(Blacklist.class);
                                idStringBlackListReceiver.add(follows.getIdUser());
                            }
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

    FormatNumber formatNumber = new FormatNumber();

    public void loadData() {
        Query query = FirebaseDatabase.getInstance().getReference("Post");
        query.orderByChild("timecreated").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPostAdapter.clear();
                long countSumChild = dataSnapshot.getChildrenCount();
                long countt = 0;
                List<Post> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    countt++;
                    long finalCountt = countt;
                    getListU(post.getIdUser());
                    if(!idStringBlackListSender.contains(post.getIdUser()) && !idStringBlackListReceiver.contains(FirebaseAuth.getInstance().getUid())) // Không có trong black list
                    {
                        assert post != null;
                        mDatabase.child(post.getIdUser()).keepSynced(false);
                        mDatabase.child(post.getIdUser()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    us = snapshot.getValue(Users.class);
                                    post.setUserPost(us);
                                    list.add(post);
                                    if (finalCountt == countSumChild) {
                                        listPostAdapter.addAll(list);
                                        listPostAdapter.reverse();
                                        recycleview.setAdapter(listPostAdapter);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        mCommentDatabase.child(post.getId()).keepSynced(false);
                        mCommentDatabase.child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    long count = snapshot.getChildrenCount();
                                    post.setSumComments((int) count);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        mInteractDatabase.child(post.getId()).keepSynced(false);
                        mInteractDatabase.child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    long count = snapshot.getChildrenCount();
                                    post.setSumLove((int) count);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
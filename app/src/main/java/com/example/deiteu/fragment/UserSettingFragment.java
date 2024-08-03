package com.example.deiteu.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.os.Bundle;

import android.content.ClipboardManager;
import android.content.ClipData;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.activity.create_post;
import com.example.deiteu.activity.information_general;
import com.example.deiteu.adapter.ListPostAdapter;
import com.example.deiteu.model.Post;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private LinearLayout layout_ttv3;
    private ImageButton btnsetting;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private AppCompatButton btn_create_post, btn_datejoin;

    private TextView tv_fullname, tv_idU, tv_description, tv_age, tv_text;

    private ImageView img_background, btn_coppy, imggender;

    private CircleImageView img_avatar;
    private ImageView btn_addPost;

    private RecyclerView recyclerView;
    private ListPostAdapter listPostAdapter;
    private ScrollView scrollview;

    int currentScrollY = 0;

    private DatabaseReference mDatabase;
    private DatabaseReference mdaDatabaseReference;
    private SharedPreferences sharedPreferences;
    Users userCurrent = new Users();

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public UserSettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserSettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSettingFragment newInstance(String param1, String param2) {
        UserSettingFragment fragment = new UserSettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnsetting = view.findViewById(R.id.buttonsetting);
        btn_create_post = view.findViewById(R.id.btn_create_post);
        img_background = view.findViewById(R.id.img_background);
        img_avatar = view.findViewById(R.id.img_avatar);
        imggender = view.findViewById(R.id.imggender);
        tv_age = view.findViewById(R.id.tv_age);
        tv_text = view.findViewById(R.id.tv_text);
        // recycle view
        btn_coppy = view.findViewById(R.id.imgcoppy_id);
        btn_datejoin = view.findViewById(R.id.btn_joindate);
        tv_idU = view.findViewById(R.id.tvidU);
        tv_fullname = view.findViewById(R.id.tvfullnameU);
        tv_idU.setText(FirebaseAuth.getInstance().getUid());
        tv_description = view.findViewById(R.id.desyourself);
        //
        btn_addPost = view.findViewById(R.id.btn_addPost);
        btn_addPost.setVisibility(View.GONE);
        scrollview = view.findViewById(R.id.scrollview);
        //
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(FirebaseAuth.getInstance().getUid()));


        btn_coppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo đối tượng ClipboardManager
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);

                // Thiết lập bộ nhớ tạm với đoạn văn bản cần sao chép
                String textToCopy = tv_idU.getText().toString();
                ClipData clipData = ClipData.newPlainText("text", textToCopy);
                // Sao chép dữ liệu vào bộ nhớ tạm
                clipboardManager.setPrimaryClip(clipData);

                // Thông báo cho người dùng biết rằng đoạn văn bản đã được sao chép
                Toast.makeText(getContext(), "Đã sao chép nội dung", Toast.LENGTH_SHORT).show();

            }
        });
        //
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userCurrent = snapshot.getValue(Users.class);
                    // avatar
                    if (userCurrent.getAvatar() != null) {
                        Picasso.get().load(userCurrent.getAvatar()).into(img_avatar);
                    }
                    // gender
                    if(userCurrent.getGender() != null)
                    {
                        switch (userCurrent.getGender()) {
                            case "1":
                                imggender.setImageResource(R.drawable.icon_male);
                                break;
                            case "2":
                                imggender.setImageResource(R.drawable.icon_female);
                                break;
                            default: imggender.setImageResource(R.drawable.icon_twogender2);
                        }
                    }
                    // birthday
                    if (userCurrent.getBirthday() != null && !userCurrent.getBirthday().equals("?? / ?? / ????")) {
                        String birthday = userCurrent.getBirthday();
                        int age = calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
                        tv_age.setText(String.valueOf(age));
                    } else {
                        tv_age.setText("");
                    }
                    // img_background
                    if (userCurrent.getBackground() != null) {
                        Picasso.get().load(userCurrent.getBackground()).into(img_background);
                    }
                    // fullname
                    if (userCurrent.getFullname() != null) {
                        tv_fullname.setText(userCurrent.getFullname());
                    } else {
                        tv_fullname.setText("Unknown Name");
                    }
                    // descript
                    if (userCurrent.getDescription() != null && !userCurrent.getDescription().equals("")) {
                        tv_description.setText(userCurrent.getDescription());
                    } else {
                        tv_description.setText("Người này là một người bí ẩn.");
                    }
                    // join date
                    if (userCurrent.getJoindate() != null) {
                        joindate = userCurrent.getJoindate();
                        day = joindate.substring(0, 2);
                        month = joindate.substring(3, 5);
                        year = joindate.substring(6, joindate.length());
                        // Sử dụng giá trị  ở đây
                    }
                    if (!joindate.equals("")) {
                        btn_datejoin.setText("Tham gia " + day + " tháng " + month + " năm " + year);
                    } else {
                        btn_datejoin.setText("Tham gia ngày ?? tháng ?? năm ????");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        scrollview.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollY = scrollview.getScrollY();
                int height = scrollview.getHeight();
                int scrollViewHeight = scrollview.getChildAt(0).getHeight();
                if (scrollViewHeight > height && (scrollY > 0 && scrollY < scrollViewHeight - height)) {
                    // ScrollView đang được cuộn
                    btn_addPost.setVisibility(View.GONE);
                } else {
                    // ScrollView không được cuộn
                    // Không làm gì cả
                    if(listPostAdapter.getItemCount() > 0)
                    {
                        btn_addPost.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.image_details);
                ImageView img = dialog.findViewById(R.id.photo_view);
                AppCompatButton btn_close = dialog.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                // Lấy đối tượng Window của dialog
                Window window = dialog.getWindow();

                if (window != null) {
                    // Set các thuộc tính cho dialog
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
                    window.setGravity(Gravity.CENTER);
                }
                Picasso.get().load(userCurrent.getAvatar()).into(img);
                dialog.show();
            }
        });
        img_background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(view.getContext());
                dialog.setContentView(R.layout.image_details);
                ImageView img = dialog.findViewById(R.id.photo_view);
                AppCompatButton btn_close = dialog.findViewById(R.id.btn_close);
                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                // Lấy đối tượng Window của dialog
                Window window = dialog.getWindow();

                if (window != null) {
                    // Set các thuộc tính cho dialog
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.alpha(0)));
                    window.setGravity(Gravity.CENTER);
                }
                Picasso.get().load(userCurrent.getBackground()).into(img);
                dialog.show();
            }
        });


        btnsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), information_general.class);
                startActivity(it);
            }
        });
        btn_create_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), create_post.class);
                startActivity(it);
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        listPostAdapter = new ListPostAdapter(getContext());
        recyclerView = view.findViewById(R.id.recycle_listpost);
        recyclerView.setAdapter(listPostAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        layoutManager.setItemPrefetchEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        layout_ttv3 = view.findViewById(R.id.layout_ttv3);
        layout_ttv3.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setNestedScrollingEnabled(false); // Mượt
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //
        btn_addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getActivity(), create_post.class);
                startActivity(it);
            }
        });
        //
        Objects.requireNonNull(recyclerView.getAdapter()).registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (recyclerView.getAdapter().getItemCount() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    btn_addPost.setVisibility(View.VISIBLE);
                } else {
                    btn_addPost.setVisibility(View.GONE);
                }
            }
        });
        String userId = FirebaseAuth.getInstance().getUid();
        Query query = FirebaseDatabase.getInstance().getReference("Post").orderByChild("idUser").equalTo(userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPostAdapter.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post.getIdUser().equals(userId)) {
                        post.setUserPost(userCurrent);
                        listPostAdapter.add(post);
                    }
                }
                listPostAdapter.reverse();
                if (listPostAdapter.getItemCount() > 0) {
                    layout_ttv3.setVisibility(View.GONE);
                    btn_addPost.setVisibility(View.VISIBLE);
//                    btn_create_post.setVisibility(View.GONE);
//                    tv_text.setVisibility(View.GONE);
                } else {
                    layout_ttv3.setVisibility(View.VISIBLE);
                    btn_addPost.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static int calculateAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        birthdate.set(year, month, day);
        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
    String day, month, year, joindate = "";
}
package com.example.deiteu.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.deiteu.R;
import com.example.deiteu.activity.DetailsMessage;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.Follows;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.model.Love;
import com.example.deiteu.model.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserCardAdapter extends BaseAdapter {

    private List<Users> userList;
    private Context context;
    private FormatNumber formatNumber = new FormatNumber();

    public UserCardAdapter(List<Users> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }
    public void add(Users user)
    {
        userList.add(user);
        notifyDataSetChanged();
    };

    public void addAll(List<Users> listU)
    {
        userList.addAll(listU);
        notifyDataSetChanged();
    };
    public void clear()
    {
        userList.clear();
        notifyDataSetChanged();
    }
    public void removeUser(int possition)
    {
        userList.remove(possition);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Users getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;
        boolean check_follow = false;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.card_user_swap, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Users user = getItem(position);
        if (user.isOnline()) {
            viewHolder.tv_status.setText("Đang online");
        } else {
            viewHolder.tv_status.setText(viewHolder.formatNumber.getTimeAgo(user.getTimelastonline()));
        }
        viewHolder.mDatabase.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users us = snapshot.getValue(Users.class);
                if(!us.isOnline())
                {
                    viewHolder.tv_status.setText(formatNumber.getTimeAgo(us.getTimelastonline()));
                }else{
                    viewHolder.tv_status.setText("Đang online");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        viewHolder.textViewName.setText(user.getFullname());
        Picasso.get().load(user.getAvatar()).into(viewHolder.image_avatarUser);
        viewHolder.mDatabase.child(user.getId()).child("birthday").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String birthday = snapshot.getValue().toString();
                    if (!birthday.equals("") && !birthday.equals("?? / ?? / ????")) {
                        int age = viewHolder.formatNumber.calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
                        viewHolder.tv_age.setText("" + age);
                    } else {
                        viewHolder.tv_age.setText("?");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        View finalView = view;
        viewHolder.mDatabase.child(user.getId()).child("gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Sử dụng giá trị  ở đây
                if (snapshot.exists()) {
                    String gender = snapshot.getValue().toString();
                    switch (gender) {
                        case "0":
                            viewHolder.imgview_gender.setImageResource(R.drawable.icon_twogender2);
                            break;
                        case "1":
                            viewHolder.imgview_gender.setImageResource(R.drawable.icon_male);
                            viewHolder.imgview_gender.setColorFilter(ContextCompat.getColor(finalView.getContext(), R.color.green_custom), PorterDuff.Mode.SRC_IN);
                            break;
                        case "2":
                            viewHolder.imgview_gender.setImageResource(R.drawable.icon_female);
                            viewHolder.imgview_gender.setColorFilter(ContextCompat.getColor(finalView.getContext(), R.color.pink_custom), PorterDuff.Mode.SRC_IN);
                            break;
                        default:
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });
        String idUserCurrent = FirebaseAuth.getInstance().getUid();
        viewHolder.mFollowsDatabase.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    viewHolder.imgview_follow.setVisibility(View.VISIBLE);
                    boolean isFollowed = false;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Follows fl = snapshot1.getValue(Follows.class);
                        if (fl.getIdFollower().equals(idUserCurrent)) // da follow
                        {
                            isFollowed = true;
                        }
                    }
                    if (isFollowed) {
                        viewHolder.imgview_follow.setImageResource(R.drawable.icon_messagenew);
                        viewHolder.check_follow_to_chat = true;
                    } else {
                        viewHolder.imgview_follow.setImageResource(R.drawable.icon_plusnew);
                        viewHolder.check_follow_to_chat = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Follows
        viewHolder.imgview_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewHolder.check_follow_to_chat) {
                    HashMap hash = new HashMap();
                    String radomid = UUID.randomUUID().toString();
                    hash.put("id", radomid);
                    hash.put("idFollower", idUserCurrent);
                    hash.put("timeFollow", viewHolder.formatNumber.getTimeCurrent());
                    viewHolder.mFollowsDatabase.child(user.getId()).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Bạn đang theo dõi người này.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Intent it = new Intent(context, DetailsMessage.class);
                    it.putExtra("idReceiver", user.getId());
                    context.startActivity(it);
                }
            }
        });
        viewHolder.imgview_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert idUserCurrent != null;
                viewHolder.mBlackListDatabase.child(idUserCurrent).keepSynced(false);
                viewHolder.mBlackListDatabase.child(idUserCurrent).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap hash = new HashMap();
                        String radomid = UUID.randomUUID().toString();
                        hash.put("id",radomid);
                        hash.put("idUser", user.getId());
                        hash.put("timeCreated", viewHolder.formatNumber.getTimeCurrent());
                        if(snapshot.exists()) // Đã có
                        {
                            for (DataSnapshot dataSnapshot :snapshot.getChildren())
                            {
                                Blacklist bl = dataSnapshot.getValue(Blacklist.class);
                                if(!bl.getIdUser().equals(user.getId()))
                                {
                                    viewHolder.mBlackListDatabase.child(idUserCurrent).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(context, "Đã thêm người này vào sổ đen.", Toast.LENGTH_SHORT).show();
                                            removeUser(position);
                                            notifyDataSetChanged();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                break;
                            }
                        }else{ // Chưa có
                            viewHolder.mBlackListDatabase.child(idUserCurrent).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Đã thêm người này vào sổ đen.", Toast.LENGTH_SHORT).show();
                                    removeUser(position);
                                    notifyDataSetChanged();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        // event on imgview_addlistlove
        assert idUserCurrent != null;
        viewHolder.mLoveListDatabase.child(idUserCurrent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean check = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Love love = dataSnapshot.getValue(Love.class);
                        if (user.getId().equals(love.getIdUser())) {
                            check = true;
                        }
                    }
                    if (check) {
                        viewHolder.imgview_addlistlove.setImageResource(R.drawable.icon_lovefull);
                    }else{
                        viewHolder.imgview_addlistlove.setImageResource(R.drawable.icon_love);
                    }
                }else{
                    viewHolder.imgview_addlistlove.setImageResource(R.drawable.icon_love);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        viewHolder.imgview_addlistlove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.addremoveUsertoLoveList(idUserCurrent,user.getId());
            }
        });
        return view;
    }

    static class ViewHolder {
        private boolean check_follow_to_chat = false;
        ImageView image_avatarUser, imgview_gender, imgview_remove, imgview_follow, imgview_addlistlove;
        TextView textViewName, tv_age, tv_status;
        private FormatNumber formatNumber = new FormatNumber();

        private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        private DatabaseReference mFollowsDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Follows");
        private DatabaseReference mLoveListDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Love");
        private DatabaseReference mBlackListDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Blacklist");

        boolean isadded = false;

        ViewHolder(View view) {
            image_avatarUser = view.findViewById(R.id.image_avatarUser);
            textViewName = view.findViewById(R.id.textViewName);
            tv_age = view.findViewById(R.id.tv_age);
            imgview_gender = view.findViewById(R.id.imgview_gender);
            imgview_remove = view.findViewById(R.id.imgview_remove);
            imgview_follow = view.findViewById(R.id.imgview_follow);
            imgview_addlistlove = view.findViewById(R.id.imgview_addlistlove);
            tv_status = view.findViewById(R.id.tv_status);
        }

        public boolean addremoveUsertoLoveList(String idUserCurrent, String idUserlove) {
            HashMap hash = new HashMap();
            String radomid = UUID.randomUUID().toString();
            hash.put("id", radomid);
            hash.put("idUser", idUserlove);
            hash.put("timeCreated", formatNumber.getTimeCurrent());
            mLoveListDatabase.child(idUserCurrent).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        boolean check = false;
                        String idLove = "";
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Love love = dataSnapshot.getValue(Love.class);
                            if (love.getIdUser().equals(idUserlove)) {
                                check =true;
                                idLove = love.getId();
                            }
                        }
                        if(check)
                        {
                            mLoveListDatabase.child(idUserCurrent).child(idLove).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(imgview_addlistlove.getContext(), "Đã xóa người dùng này ra khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
                                    isadded = false;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(imgview_addlistlove.getContext(), "Đã có lỗi xảy ra\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            mLoveListDatabase.child(idUserCurrent).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(imgview_addlistlove.getContext(), "Đã thêm người dùng này vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                                    isadded = true;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(imgview_addlistlove.getContext(), "Đã có lỗi xảy ra\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Not exist, add value
                        mLoveListDatabase.child(idUserCurrent).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(imgview_addlistlove.getContext(), "Đã thêm người dùng này vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
                                isadded = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(imgview_addlistlove.getContext(), "Đã có lỗi xảy ra\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            return isadded;
        }

        public void addUsertoBlackList() {

        }
    }
}


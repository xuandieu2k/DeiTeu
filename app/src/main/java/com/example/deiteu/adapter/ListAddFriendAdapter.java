package com.example.deiteu.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.activity.DetailsMessage;
import com.example.deiteu.activity.Details_personal;
import com.example.deiteu.model.Follows;
import com.example.deiteu.model.FormatNumber;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListAddFriendAdapter extends RecyclerView.Adapter<ListAddFriendAdapter.MyViewHolder> {
    private Context context;
    private List<Users> usersList;

    public ListAddFriendAdapter(Context context) {
        this.context = context;
        usersList = new ArrayList<>();
    }

    public void add(Users user) {
        usersList.add(user);
        notifyDataSetChanged();
    }

    ;

    public void addAll(List<Users> listU) {
        usersList.addAll(listU);
        notifyDataSetChanged();
    }

    ;

    public void removeObject(Users us) {
        usersList.remove(us);
        notifyDataSetChanged();
    }

    public void clear() {
        usersList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListAddFriendAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_add_user,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAddFriendAdapter.MyViewHolder holder, int position) {
        Users us = usersList.get(position);
        String idUCurrent = FirebaseAuth.getInstance().getUid();
        if (us.getAvatar() != null) {
            Picasso.get().load(us.getAvatar()).into(holder.img_avatarchat);
        }
        if (us.getFullname() != null) {
            holder.txtUfullname.setText(us.getFullname());
        }
        if (us.getGender() != null) {
            switch (us.getGender()) {
                case "0":
                    holder.img_gender.setImageResource(R.drawable.icon_twogender2);
                    break;
                case "1":
                    holder.img_gender.setImageResource(R.drawable.icon_male);
                    holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.green_custom), PorterDuff.Mode.SRC_IN);
                    break;
                case "2":
                    holder.img_gender.setImageResource(R.drawable.icon_female);
                    holder.img_gender.setColorFilter(ContextCompat.getColor(context, R.color.pink_custom), PorterDuff.Mode.SRC_IN);
                    break;
                default:
            }
        }
        if (us.getBirthday() != null) {
            String birthday = us.getBirthday();
            int age = holder.formatNumber.calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
            holder.tv_age.setText(String.valueOf(age));
        }
        if (us.isOnline()) {
            holder.txtStatus.setText("Đang online");
            holder.img_active.setVisibility(View.VISIBLE);
        } else {
            holder.txtStatus.setText(holder.formatNumber.getTimeAgo(us.getTimelastonline()));
            holder.img_active.setVisibility(View.GONE);
        }
        holder.mUserDatabase.child(us.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users us = snapshot.getValue(Users.class);
                if (!us.isOnline()) {
                    holder.img_active.setVisibility(View.GONE);
                    holder.txtStatus.setText(holder.formatNumber.getTimeAgo(us.getTimelastonline()));
                } else {
                    holder.txtStatus.setText("Đang online");
                    holder.img_active.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.img_avatarchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!idUserCurrent.equals(post.getIdUser()))
//                {
                Intent it = new Intent(view.getContext(), Details_personal.class);
                it.putExtra("idUserdt", us.getId());
                it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(it);
//                }
            }
        });
//        holder.btn_follow_chat.setText("Theo dõi");
        holder.mFollowDatabase.child(us.getId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot data : snapshot.getChildren())
                    {
                        Follows follows = data.getValue(Follows.class);
                        assert follows != null;
                        if(follows.getIdFollower().equals(idUCurrent))
                        {
                            holder.btn_follow_chat.setText("Hủy theo dõi");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = holder.btn_follow_chat.getText().toString().trim();
                if (text.equals("Theo dõi")) {
                    HashMap hash = new HashMap();
                    String radomid = UUID.randomUUID().toString();
                    hash.put("id", radomid);
                    hash.put("idFollower", idUCurrent);
                    hash.put("timeFollow", holder.formatNumber.getTimeCurrent());
                    holder.mFollowDatabase.child(us.getId()).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Bạn đang theo dõi người này.", Toast.LENGTH_SHORT).show();
                            holder.btn_follow_chat.setText("Hủy theo dõi");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    final Dialog dialogCompat = new Dialog(view.getContext());
                    dialogCompat.setContentView(R.layout.dialog_confirmlist);
                    AppCompatButton btn_oke = dialogCompat.findViewById(R.id.btn_ok);
                    AppCompatButton btn_cancel = dialogCompat.findViewById(R.id.btn_cancel);
                    TextView tv = dialogCompat.findViewById(R.id.tv_title);
                    tv.setText("Bạn có muốn hủy theo dõi người này không?");
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialogCompat.dismiss();
                        }
                    });
                    String idUser = FirebaseAuth.getInstance().getUid();
                    btn_oke.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.mFollowDatabase.child(us.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot data : snapshot.getChildren()) {
                                            Follows follows = data.getValue(Follows.class);
                                            assert follows != null;
                                            if (follows.getIdFollower().equals(idUser)) {
                                                holder.mFollowDatabase.child(us.getId()).child(follows.getId()).removeValue();
                                                holder.btn_follow_chat.setText("Theo dõi");
                                                Toast.makeText(dialogCompat.getContext(), "Đã hủy theo dõi người dùng này.", Toast.LENGTH_SHORT).show();
                                                break;
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            dialogCompat.dismiss();
                        }
                    });
                    dialogCompat.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        FormatNumber formatNumber = new FormatNumber();
        CircleImageView img_avatarchat;
        ImageView img_active, img_gender;
        TextView txtUfullname, tv_age, txtStatus;
        AppCompatButton btn_follow_chat;
        public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        public DatabaseReference mFollowDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Follows");

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img_avatarchat = itemView.findViewById(R.id.img_avatarchat);
            img_active = itemView.findViewById(R.id.img_active);
            img_gender = itemView.findViewById(R.id.img_gender);
            txtUfullname = itemView.findViewById(R.id.txtUfullname);
            tv_age = itemView.findViewById(R.id.tv_age);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btn_follow_chat = itemView.findViewById(R.id.btn_follow_chat);


        }
    }
}

package com.example.deiteu.adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.DialogCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.activity.DetailsMessage;
import com.example.deiteu.activity.Details_personal;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.Follows;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.model.Love;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListUserSearchAdapter extends RecyclerView.Adapter<ListUserSearchAdapter.MyViewHolder> {
    private List<Users> listUser;
    private Context context;
    private int tab;
    private FormatNumber formatNumber = new FormatNumber();

    public ListUserSearchAdapter(Context context, int tab) {
        this.context = context;
        listUser = new ArrayList<>();
        this.tab = tab;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void add(Users user) {
        listUser.add(user);
        notifyDataSetChanged();
    }

    ;

    @SuppressLint("NotifyDataSetChanged")
    public void addAll(List<Users> listU) {
        listUser.addAll(listU);
        notifyDataSetChanged();
    }

    ;

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        listUser.clear();
        notifyDataSetChanged();
    }

    public List<Users> getListUser()
    {
        return listUser;
    }

    @NonNull
    @Override
    public ListUserSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user_search, parent, false);
        return new ListUserSearchAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserSearchAdapter.MyViewHolder holder, int position) {
        switch (tab) {
            case 0:
                processFollowing(holder, position);
                break;
            case 1:
                processIsFollow(holder, position);
                break;
            case 2:
                processLove(holder, position);
                break;
            case 3:
                processBlacklist(holder, position);
                break;
            default:
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void processFollowing(@NonNull ListUserSearchAdapter.MyViewHolder holder, int position) {
        Users user = listUser.get(position);
        String idUser = FirebaseAuth.getInstance().getUid();
        holder.btn_follow_chat.setBackground(context.getDrawable(R.drawable.box_cancel));
        holder.btn_follow_chat.setText("Hủy theo dõi");
        holder.btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogCompat = new Dialog(context);
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
                        holder.mFollowDatabase.child(user.getId()).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        Follows follows = data.getValue(Follows.class);
                                        assert follows != null;
                                        if (follows.getIdFollower().equals(idUser)) {
                                            holder.mFollowDatabase.child(user.getId()).child(follows.getId()).removeValue();
                                            Toast.makeText(context, "Đã hủy theo dõi người dùng này.", Toast.LENGTH_SHORT).show();
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
        });
        setInforUser(holder, position);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void processIsFollow(@NonNull ListUserSearchAdapter.MyViewHolder holder, int position) {
        Users user = listUser.get(position);
        holder.btn_follow_chat.setBackground(context.getDrawable(R.drawable.box_unlove));
        holder.btn_follow_chat.setText("Nhắn tin");
        holder.btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(context, DetailsMessage.class);
                it.putExtra("idReceiver", user.getId());
                context.startActivity(it);
            }
        });
        setInforUser(holder, position);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void processLove(@NonNull ListUserSearchAdapter.MyViewHolder holder, int position) {
        Users us = listUser.get(position);
        holder.btn_follow_chat.setBackground(context.getDrawable(R.drawable.box_cancel));
        holder.btn_follow_chat.setText("Bỏ thích");
        holder.btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogCompat = new Dialog(context);
                dialogCompat.setContentView(R.layout.dialog_confirmlist);
                AppCompatButton btn_oke = dialogCompat.findViewById(R.id.btn_ok);
                AppCompatButton btn_cancel = dialogCompat.findViewById(R.id.btn_cancel);
                TextView tv = dialogCompat.findViewById(R.id.tv_title);
                tv.setText("Bạn có muốn xóa người này khỏi danh sách yêu thích không?");
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
                        assert idUser != null;
                        holder.mLoveDatabase.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Love bl = dataSnapshot.getValue(Love.class);
                                        if (bl.getIdUser().equals(us.getId())) {
                                            holder.mLoveDatabase.child(idUser).child(bl.getId()).removeValue();
                                            Toast.makeText(context, "Đã xóa người dùng khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
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
        });
        setInforUser(holder, position);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void processBlacklist(@NonNull ListUserSearchAdapter.MyViewHolder holder, int position) {
        Users us = listUser.get(position);
        holder.btn_follow_chat.setBackground(context.getDrawable(R.drawable.box_cancel));
        holder.btn_follow_chat.setText("Gỡ bỏ");
        holder.btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialogCompat = new Dialog(context);
                dialogCompat.setContentView(R.layout.dialog_confirmlist);
                AppCompatButton btn_oke = dialogCompat.findViewById(R.id.btn_ok);
                AppCompatButton btn_cancel = dialogCompat.findViewById(R.id.btn_cancel);
                TextView tv = dialogCompat.findViewById(R.id.tv_title);
                tv.setText("Bạn có muốn xóa người này khỏi sổ đen không?");
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
                        assert idUser != null;
                        holder.mBlacklistDatabase.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        Blacklist bl = dataSnapshot.getValue(Blacklist.class);
                                        if (bl.getIdUser().equals(us.getId())) {
                                            holder.mBlacklistDatabase.child(idUser).child(bl.getId()).removeValue();
                                            Toast.makeText(context, "Đã xóa người dùng khỏi sổ đen.\nGiờ đây bạn có thể tương tác với người dùng này.", Toast.LENGTH_SHORT).show();
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
        });
        setInforUser(holder, position);
    }

    public void setInforUser(@NonNull ListUserSearchAdapter.MyViewHolder holder, int position) {
        Users us = listUser.get(position);
        holder.mUserDatabase.child(us.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users us = snapshot.getValue(Users.class);
                if (!us.isOnline()) {
                    holder.img_active.setVisibility(View.GONE);
                    holder.txtStatus.setText(formatNumber.getTimeAgo(us.getTimelastonline()));
                } else {
                    holder.txtStatus.setText("Đang online");
                    holder.img_active.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (us.getFullname() == null) {
            us.setFullname("Unknown name");
        }
        holder.name.setText(us.getFullname());
        if (us.getAvatar() != null) {
            Picasso.get().load(us.getAvatar()).into(holder.img_avatarchat);
        }
        if (us.isOnline()) {
            holder.txtStatus.setText("Đang online");
            holder.img_active.setVisibility(View.VISIBLE);
        } else {
            holder.txtStatus.setText(formatNumber.getTimeAgo(us.getTimelastonline()));
            holder.img_active.setVisibility(View.GONE);
        }
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
    }


    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView txtStatus;
        private ImageView img_active;
        AppCompatButton btn_follow_chat;

        public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        public DatabaseReference mFollowDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Follows");
        public DatabaseReference mLoveDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Love");
        public DatabaseReference mBlacklistDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Blacklist");

        private final CircleImageView img_avatarchat;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtUfullname);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            img_avatarchat = itemView.findViewById(R.id.img_avatarchat);
            img_active = itemView.findViewById(R.id.img_active);
            btn_follow_chat = itemView.findViewById(R.id.btn_follow_chat);
        }
    }
}

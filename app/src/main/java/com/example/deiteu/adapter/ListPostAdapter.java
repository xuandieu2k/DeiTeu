package com.example.deiteu.adapter;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.activity.DetailsMessage;
import com.example.deiteu.activity.Details_Post;
import com.example.deiteu.activity.Details_personal;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.Comments;
import com.example.deiteu.model.Follows;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.model.Interact;
import com.example.deiteu.model.Love;
import com.example.deiteu.model.Notification;
import com.example.deiteu.model.Post;
import com.example.deiteu.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListPostAdapter extends RecyclerView.Adapter<ListPostAdapter.MyViewHolder> {
    private Context context;
    private List<Post> listPost;

    private FormatNumber formatNumber = new FormatNumber();

    public ListPostAdapter(Context context) {
        this.context = context;
        listPost = new ArrayList<>();
    }

    public void add(Post post) {
        listPost.add(post);
        notifyDataSetChanged();
    }

    public void addAll(List<Post> listpost) {
        listPost.addAll(listpost);
    }


    public void reverse() {
        Collections.reverse(listPost);
        notifyDataSetChanged();
    }

    public void clear() {
        listPost.clear();
        notifyDataSetChanged();
    }

    public void sortByTimeCreated() {
        Collections.sort(listPost, new Comparator<Post>() {
            @Override
            public int compare(Post post1, Post post2) {
                return Long.compare(post2.getTimecreated(), post1.getTimecreated());
            }
        });
        notifyDataSetChanged();
    }

    public List<Post> getListPost() {
        return listPost;
    }

    public void setListPost(List<Post> listPost) {
        this.listPost = listPost;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.box_post_user, parent, false);
        return new ListPostAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Post post = listPost.get(position);
        getListU(holder,position);
        String idUserCurrent = FirebaseAuth.getInstance().getUid();
        if (post.getImage() != null) {
            Picasso.get().load(post.getImage()).into(holder.imgpost);
        } else {
            holder.imgpost.setVisibility(View.GONE);
        }
        if (post.getStatus() != null) {
            holder.tvtextpost.setText(post.getStatus());
        } else {
            holder.tvtextpost.setVisibility(View.GONE);
        }
        if (post.getUserPost().getAvatar() != null) {
            Picasso.get().load(post.getUserPost().getAvatar()).into(holder.circle_avatar);
        }
        if (post.getUserPost().getFullname() != null) {
            holder.tvfullnameUser.setText(post.getUserPost().getFullname());
        }
        if (post.getUserPost().getGender() != null) {
            switch (post.getUserPost().getGender()) {
                case "0":
                    holder.imgbtn_gender.setImageResource(R.drawable.icon_twogender2);
                    break;
                case "1":
                    holder.imgbtn_gender.setImageResource(R.drawable.icon_male);
                    holder.imgbtn_gender.setColorFilter(ContextCompat.getColor(context, R.color.green_custom), PorterDuff.Mode.SRC_IN);
                    break;
                case "2":
                    holder.imgbtn_gender.setImageResource(R.drawable.icon_female);
                    holder.imgbtn_gender.setColorFilter(ContextCompat.getColor(context, R.color.pink_custom), PorterDuff.Mode.SRC_IN);
                    break;
                default:
            }
        }
        if (post.getUserPost().getBirthday() != null) {
            String birthday = post.getUserPost().getBirthday();
            int age = calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
            holder.tv_age.setText(String.valueOf(age));
        }
        holder.tv_sumcomment.setText(formatNumber.formatFollowersCount(post.getSumComments()));
        holder.tv_sumlove.setText(formatNumber.formatFollowersCount(post.getSumLove()));
        // Interact
        holder.mInteractDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean check = false;
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Interact interact = dataSnapshot.getValue(Interact.class);
                        if (interact.getIdUser().equals(idUserCurrent)) {
                            check = true;
                        }
                    }
                    if (check) {
                        holder.btnlove.setBackground(context.getDrawable(R.drawable.icon_lovefull));
                    } else {
                        holder.btnlove.setBackground(null);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btnlove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap hash = new HashMap();
                String radomid = UUID.randomUUID().toString();
                hash.put("id", radomid);
                hash.put("idUser", idUserCurrent);
                hash.put("idPost", post.getId());
                hash.put("timecreated", getTimeCurrent());
                holder.mInteractDatabase.child(post.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Interact interact = new Interact();
                            boolean isInterect = false;
                            for (DataSnapshot dt : snapshot.getChildren()) {
                                Interact it = dt.getValue(Interact.class);
                                if (it.getIdUser().equals(idUserCurrent)) {
                                    isInterect = true;
                                    interact = it;
                                }
                            }
                            if (isInterect) {
                                holder.mInteractDatabase.child(post.getId()).child(interact.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        holder.btnlove.setBackground(context.getDrawable(R.drawable.icon_love));
                                        removeNotification(holder,post.getIdUser(),post.getId());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                holder.mInteractDatabase.child(post.getId()).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @SuppressLint("UseCompatLoadingForDrawables")
                                    @Override
                                    public void onSuccess(Void unused) {
                                        holder.btnlove.setBackground(context.getDrawable(R.drawable.icon_lovefull));
                                        addNotification(holder,post.getIdUser(),post.getId());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            holder.mInteractDatabase.child(post.getId()).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @SuppressLint("UseCompatLoadingForDrawables")
                                @Override
                                public void onSuccess(Void unused) {
                                    holder.btnlove.setBackground(context.getDrawable(R.drawable.icon_lovefull));
                                    addNotification(holder,post.getIdUser(),post.getId());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, "Đã có lỗi xảy ra.\nVui lòng thử lại", Toast.LENGTH_SHORT).show();
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
        holder.mInteractDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long count = snapshot.getChildrenCount();
                    holder.tv_sumlove.setText(formatNumber.formatFollowersCount((int) count));
                } else {
                    holder.tv_sumlove.setText("0");
                    holder.btnlove.setBackground(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.mCommentDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long count = snapshot.getChildrenCount();
                    holder.tv_sumcomment.setText(formatNumber.formatFollowersCount((int) count));
                } else {
                    holder.tv_sumcomment.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //
        if (post.getUserPost().isOnline()) {
            holder.tvstatusUser.setText("Đang online");
            holder.img_active.setVisibility(View.VISIBLE);
        } else {
            holder.tvstatusUser.setText(formatNumber.getTimeAgo(post.getUserPost().getTimelastonline()));
            holder.img_active.setVisibility(View.GONE);
        }
        holder.mUserDatabase.child(post.getIdUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users us = snapshot.getValue(Users.class);
                if (!us.isOnline()) {
                    holder.tvstatusUser.setText(formatNumber.getTimeAgo(us.getTimelastonline()));
                    holder.img_active.setVisibility(View.GONE);
                } else {
                    holder.tvstatusUser.setText("Đang online");
                    holder.img_active.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btncomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Details_Post.class);
                intent.putExtra("keyidpost", listPost.get(position).getId());
                intent.putExtra("keyidUserpost", listPost.get(position).getIdUser());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.mFollowsDatabase.child(post.getIdUser()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (!snapshot.getKey().equals(idUserCurrent)) // snapshot.getKey() user duoc follow
                    {
                        holder.btn_follow_chat.setVisibility(View.VISIBLE);
                        holder.imgbtn_seemore.setVisibility(View.GONE);
                        boolean isFollowed = false;
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Follows fl = snapshot1.getValue(Follows.class);
                            if (fl.getIdFollower().equals(idUserCurrent)) // da follow
                            {
                                isFollowed = true;
                            }
                        }
                        if (isFollowed) {
                            holder.btn_follow_chat.setText("Nhắn tin");
                        } else {
                            holder.btn_follow_chat.setText("Theo dõi");
                        }

                    } else {
                        holder.btn_follow_chat.setVisibility(View.GONE);
                        holder.imgbtn_seemore.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (idUserCurrent.equals(post.getIdUser())) {
            holder.btn_follow_chat.setVisibility(View.GONE);
            holder.imgbtn_seemore.setVisibility(View.GONE);
        }
        holder.btn_follow_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = holder.btn_follow_chat.getText().toString();
                if (text.equals("Nhắn tin")) {
                    Intent it = new Intent(context, DetailsMessage.class);
                    it.putExtra("idReceiver", post.getIdUser());
                    it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    view.getContext().startActivity(it);
                } else {
                    HashMap hash = new HashMap();
                    String radomid = UUID.randomUUID().toString();
                    hash.put("id", radomid);
                    hash.put("idFollower", idUserCurrent);
                    hash.put("timeFollow", getTimeCurrent());
                    holder.mFollowsDatabase.child(post.getIdUser()).child(radomid).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                }
            }
        });
        holder.circle_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!idUserCurrent.equals(post.getIdUser()))
//                {
                Intent it = new Intent(view.getContext(), Details_personal.class);
                it.putExtra("idUserdt", post.getIdUser());
                it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(it);
//                }
            }
        });
        holder.imgpost.setOnClickListener(new View.OnClickListener() {
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
                Picasso.get().load(post.getImage()).into(img);
                dialog.show();
            }
        });
        holder.imgbtn_seemore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View bottomSheetView = bottomSheetDialog.getLayoutInflater().inflate(R.layout.dialog_removepost, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                AppCompatButton btn_report = bottomSheetDialog.findViewById(R.id.btn_report);
                AppCompatButton btn_remove = bottomSheetDialog.findViewById(R.id.btn_remove);
                btn_remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialogCompat = new Dialog(context);
                        dialogCompat.setContentView(R.layout.dialog_confirmlist);
                        AppCompatButton btn_oke = dialogCompat.findViewById(R.id.btn_ok);
                        AppCompatButton btn_cancel = dialogCompat.findViewById(R.id.btn_cancel);
                        TextView tv = dialogCompat.findViewById(R.id.tv_title);
                        tv.setText("Bài viết sẽ bị xóa\nBạn có muốn xóa không?");
                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogCompat.dismiss();
                            }
                        });
                        String idUser = FirebaseAuth.getInstance().getUid();
                        btn_oke.setOnClickListener(new View.OnClickListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onClick(View view) {
                                if (post.getImage() != null) {
                                    holder.storageref = FirebaseStorage.getInstance("gs://deiteu-0905.appspot.com").getReferenceFromUrl(post.getImage());
                                    holder.storageref.delete();
                                    holder.mInteractDatabase.child(post.getId()).removeValue();
//                                    holder.commentstorageref.child()
                                    holder.mCommentDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Comments comments = dataSnapshot.getValue(Comments.class);
                                                    if (comments.getImage() != null) {
                                                        holder.commentstorageref = FirebaseStorage.getInstance("gs://deiteu-0905.appspot.com").getReferenceFromUrl(comments.getImage());
                                                        holder.commentstorageref.delete();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    holder.mCommentDatabase.child(post.getId()).removeValue();
                                    holder.mNotificatonDatabase.child(idUserCurrent).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren())
                                            {
                                                Notification notification = dataSnapshot.getValue(Notification.class);
                                                if(notification.getIdPost().equals(post.getId()))
                                                {
                                                    holder.mNotificatonDatabase.child(idUserCurrent).child(notification.getId()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    holder.mPostDatabase.child(post.getId()).removeValue();
                                    Toast.makeText(context, "Bài viết của bạn đã được xóa", Toast.LENGTH_SHORT).show();
                                    listPost.remove(post);
                                    notifyDataSetChanged();
                                } else {
                                    holder.mInteractDatabase.child(post.getId()).removeValue();
//                                    holder.commentstorageref.child()
                                    holder.mCommentDatabase.child(post.getId()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    Comments comments = dataSnapshot.getValue(Comments.class);
                                                    if (comments.getImage() != null) {
                                                        holder.commentstorageref = FirebaseStorage.getInstance("gs://deiteu-0905.appspot.com").getReferenceFromUrl(comments.getImage());
                                                        holder.commentstorageref.delete();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    holder.mCommentDatabase.child(post.getId()).removeValue();
                                    holder.mNotificatonDatabase.child(idUserCurrent).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot: snapshot.getChildren())
                                            {
                                                Notification notification = dataSnapshot.getValue(Notification.class);
                                                if(notification.getIdPost().equals(post.getId()))
                                                {
                                                    holder.mNotificatonDatabase.child(idUserCurrent).child(notification.getId()).removeValue();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    holder.mPostDatabase.child(post.getId()).removeValue();
                                    Toast.makeText(context, "Bài viết của bạn đã được xóa", Toast.LENGTH_SHORT).show();
                                    listPost.remove(post);
                                    notifyDataSetChanged();
                                }
                                dialogCompat.dismiss();
                                bottomSheetDialog.cancel();
                            }
                        });
                        dialogCompat.show();
                    }
                });

            }
        });
    }

    boolean removeImg, removePicCommnets = false;

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    public void addNotification(MyViewHolder holder, String idUserPost, String idPost) {
        String idUCurrent = FirebaseAuth.getInstance().getUid();
        if(!idUserPost.equals(idUCurrent))
        {
            HashMap hash = new HashMap<>();
            String radomid = UUID.randomUUID().toString();
            hash.put("id", radomid);
            hash.put("idPost", idPost);
            hash.put("idUser", idUCurrent);
            hash.put("content", "Đã thả tim bài biết của bạn");
            hash.put("readed", false);
            hash.put("type", "2");
            hash.put("timeCreated", ServerValue.TIMESTAMP);
            holder.mNotificatonDatabase.child(idUserPost).child(radomid).setValue(hash);
        }
    }

    public void removeNotification(MyViewHolder holder, String idUserPost, String idPost) {
        String idUCurrent = FirebaseAuth.getInstance().getUid();
        holder.mNotificatonDatabase.child(idUserPost).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        if (notification.getIdUser().equals(idUCurrent)
                                && notification.getIdPost().equals(idPost)
                                && notification.getType().equals("2")) {
                            holder.mNotificatonDatabase.child(idUserPost).child(notification.getId()).removeValue();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_sumlove, tv_sumcomment, tv_age;
        private ImageView imgpost;
        private TextView tvtextpost, tvfullnameUser, tvstatusUser;
        private ImageButton imgbtn_gender, btncomment, btnlove;
        private CircleImageView circle_avatar;
        private ImageView imgbtn_seemore, img_active;
        private AppCompatButton btn_follow_chat;

        public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
        public DatabaseReference mCommentDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Comments");
        private DatabaseReference mFollowsDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Follows");
        private DatabaseReference mInteractDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Interact");
        private DatabaseReference mPostDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Post");

        private DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Notification");
        private DatabaseReference mBlacklistDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Blacklist");

        private StorageReference storageref;
        private StorageReference commentstorageref;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sumlove = itemView.findViewById(R.id.tv_sumlove);
            tv_sumcomment = itemView.findViewById(R.id.tv_sumcomment);
            tvtextpost = itemView.findViewById(R.id.tvtextpost);
            tv_age = itemView.findViewById(R.id.tv_age);
            tvfullnameUser = itemView.findViewById(R.id.tvfullnameUser);
            imgpost = itemView.findViewById(R.id.imgpost);
            circle_avatar = itemView.findViewById(R.id.circle_avatar);
            imgbtn_gender = itemView.findViewById(R.id.imgbtn_gender);
            btnlove = itemView.findViewById(R.id.btnlove);
            btncomment = itemView.findViewById(R.id.btncomment);
            //
            img_active = itemView.findViewById(R.id.img_active);
            tvstatusUser = itemView.findViewById(R.id.tvstatusUser);

            btn_follow_chat = itemView.findViewById(R.id.btn_follow_chat);
            imgbtn_seemore = itemView.findViewById(R.id.imgbtn_seemore);
            btn_follow_chat.setVisibility(View.VISIBLE);
            btn_follow_chat.setText("Theo dõi");
            imgbtn_seemore.setVisibility(View.GONE);

        }
    }

    List<String> idStringBlackListReceiver = new ArrayList<>();
    List<String> idStringBlackListSender = new ArrayList<>();

    public void getListU(MyViewHolder holder, int possition) {
        Post post = listPost.get(possition);
        idStringBlackListReceiver.clear();
        idStringBlackListSender.clear();
        String idU = FirebaseAuth.getInstance().getUid();
        assert idU != null;
        holder.mBlacklistDatabase.child(idU).addValueEventListener(new ValueEventListener() { // Sender
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        Blacklist follows = data.getValue(Blacklist.class);
                        idStringBlackListSender.add(follows.getIdUser());
                    }
                }
                holder.mBlacklistDatabase.child(post.getIdUser()).addValueEventListener(new ValueEventListener() { // Receiver
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Blacklist follows = data.getValue(Blacklist.class);
                                idStringBlackListReceiver.add(follows.getIdUser());
                            }
                        }
                        if (idStringBlackListReceiver.contains(idU) || idStringBlackListSender.contains(post.getIdUser())) {
                            holder.btncomment.setClickable(false);
                            holder.btnlove.setClickable(false);
                            holder.btncomment.setImageTintList(context.getColorStateList(R.color.gray2_custom));
                            holder.btnlove.setImageTintList(context.getColorStateList(R.color.gray2_custom));
                        } else {
                            holder.btncomment.setClickable(true);
                            holder.btnlove.setClickable(true);
                            holder.btncomment.setImageTintList(context.getColorStateList(R.color.green_custom));
                            holder.btnlove.setImageTintList(context.getColorStateList(R.color.green_custom));
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

    //
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

    private String getTimeCurrent() {
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        }
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDateTime = dateTime.format(formatter);
        }
        return formattedDateTime;
    }
}

package com.example.deiteu.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.activity.Details_Post;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.model.Notification;
import com.example.deiteu.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListNotificationAdapter extends RecyclerView.Adapter<ListNotificationAdapter.MyViewholder> {
    private List<Notification> notificationList;
    private Context context;

    public ListNotificationAdapter(Context context) {
        this.context = context;
        notificationList = new ArrayList<>();
    }

    public void add(Notification notification) {
        notificationList.add(notification);
        notifyDataSetChanged();
    }

    ;

    public void addAll(List<Notification> listU) {
        notificationList.addAll(listU);
        notifyDataSetChanged();
    }

    ;

    public void removeObject(Notification notification) {
        notificationList.remove(notification);
        notifyDataSetChanged();
    }

    public void clear() {
        notificationList.clear();
        notifyDataSetChanged();
    }
    public void revese() {
        Collections.reverse(notificationList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListNotificationAdapter.MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification, parent, false);
        return new ListNotificationAdapter.MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListNotificationAdapter.MyViewholder holder, int position) {
        Notification notification = notificationList.get(position);
        Users us = notification.getUsers();
        if(us.getAvatar() != null)
        {
            Picasso.get().load(us.getAvatar()).into(holder.img_avatarchat);
        }
        if (us.getFullname() == null) {
            us.setFullname("Unknown name");
        }
        holder.txtUfullname.setText(us.getFullname());
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
        if(notification.getContent() != "")
        {
            holder.txtContent.setText(notification.getContent());
        }
        if(!notification.isReaded())
        {
            holder.tv_time.setTextColor(context.getResources().getColor(R.color.green_custom));
            holder.txtContent.setTextColor(context.getResources().getColor(R.color.green_custom));
        }
        holder.tv_time.setText(holder.formatNumber.getTimeAgo(notification.getTimeCreated()));
        setOnClickListenerslayout(holder.layout_parent,holder,position);
    }
    public void setOnClickListenerslayout(View view, @NonNull ListNotificationAdapter.MyViewholder holder,int position) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int count = viewGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                View childView = viewGroup.getChildAt(i);
                setOnClickListenerslayout(childView,holder,position);
            }
        } else {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click của phần tử v
                    updateRead(holder,notificationList.get(position),position);
                    Intent intent = new Intent(context, Details_Post.class);
                    intent.putExtra("keyidpost", notificationList.get(position).getIdPost());
                    intent.putExtra("keyidUserpost", notificationList.get(position).getIdUser());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
    Notification notificationout = new Notification();
    public void updateRead(@NonNull ListNotificationAdapter.MyViewholder holder,Notification notification,int position)
    {
        holder.tv_time.setTextColor(context.getResources().getColor(R.color.white));
        holder.txtContent.setTextColor(context.getResources().getColor(R.color.white));
        holder.mNotificatonDatabase.child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(notification.getId())
                .child("readed").setValue(true);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class MyViewholder extends RecyclerView.ViewHolder {
        FormatNumber formatNumber = new FormatNumber();
        CircleImageView img_avatarchat;
        TextView txtUfullname, tv_age, txtContent, tv_time;
        ImageView img_gender;
        RelativeLayout layout_parent;
        private DatabaseReference mNotificatonDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Notification");


        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            img_gender = itemView.findViewById(R.id.img_gender);
            img_avatarchat = itemView.findViewById(R.id.img_avatar);
            tv_age = itemView.findViewById(R.id.tv_age);
            txtUfullname = itemView.findViewById(R.id.txtUfullname);
            txtContent = itemView.findViewById(R.id.txtContent);
            tv_time = itemView.findViewById(R.id.tv_time);
            layout_parent = itemView.findViewById(R.id.layout_parent);
        }
    }
}

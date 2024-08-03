package com.example.deiteu.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.activity.Details_personal;
import com.example.deiteu.model.Comments;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListCommentAdapter extends RecyclerView.Adapter<ListCommentAdapter.MyViewHolder>{

    private Context context;
    private List<Comments> listComments;

    public ListCommentAdapter(Context context) {
        this.context = context;
        listComments =new ArrayList<>();
    }

    public void add(Comments comments)
    {
        listComments.add(comments);
        notifyDataSetChanged();
    };
    public void clear()
    {
        listComments.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListCommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.box_comment,parent,false);
        return new ListCommentAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCommentAdapter.MyViewHolder holder, int position) {
        Comments comments = listComments.get(position);
        if(comments.getImage() != null)
        {
            Picasso.get().load(comments.getImage()).into(holder.img_comment);
        }else{
            holder.img_comment.setVisibility(View.GONE);
        }
        if( comments.getContent()  != null)
        {
            holder.txt_status.setText(comments.getContent());
        }else{
            holder.txt_status.setVisibility(View.GONE);
        }
        if(comments.getUserComnent().getAvatar() != null)
        {
            Picasso.get().load(comments.getUserComnent().getAvatar()).into(holder.img_avatar_comment);
        }
        if(comments.getUserComnent().getFullname() != null)
        {
            holder.txtUfullname.setText(comments.getUserComnent().getFullname());
        }
        if(comments.getUserComnent().getGender() != null)
        {
            switch (comments.getUserComnent().getGender())
            {
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
            if(comments.getUserComnent().getBirthday() != null)
            {
                String birthday = comments.getUserComnent().getBirthday();
                int age = calculateAge(Integer.parseInt(birthday.substring(6,birthday.length())),Integer.parseInt(birthday.substring(3,5)),Integer.parseInt(birthday.substring(0,2)));
                holder.tv_age.setText(String.valueOf(age));
            }

            if(comments.getTimecreated() != null)
            {
                holder.time_comment.setText(convertTimeToText(String.valueOf(comments.getTimecreated())));
            }
        }
        holder.img_avatar_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(!idUserCurrent.equals(post.getIdUser()))
//                {
                Intent it = new Intent(view.getContext(), Details_personal.class);
                it.putExtra("idUserdt", comments.getIdUser());
                it.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                view.getContext().startActivity(it);
//                }
            }
        });
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Thực hiện việc cập nhật TextView ở đây
//                holder.time_comment.setText(convertTimeToText(String.valueOf(comments.getTimecreated())));
//                // Lặp lại sau mỗi giây
//                handler.postDelayed(this, 1000);
//            }
//        }, 1000); // Đợi 1 giây trước khi bắt đầu cập nhật
        holder.img_comment.setOnClickListener(new View.OnClickListener() {
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
                Picasso.get().load(comments.getImage()).into(img);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return  listComments.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView txtUfullname,tv_age,txt_status,time_comment;
        private ImageView img_comment,img_gender;
        private CircleImageView img_avatar_comment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUfullname = itemView.findViewById(R.id.txtUfullname);
            txt_status = itemView.findViewById(R.id.txt_status);
            time_comment = itemView.findViewById(R.id.time_comment);
            tv_age = itemView.findViewById(R.id.tv_age);
            //
            img_avatar_comment = itemView.findViewById(R.id.img_avatar_comment);
            img_comment = itemView.findViewById(R.id.img_comment);
            img_gender = itemView.findViewById(R.id.img_gender);
        }
    }

    public static int calculateAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        birthdate.set(year, month, day);
        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)){
            age--;
        }
        return age;
    }

    public static String convertTimeToText(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date convertedDate = new Date();

        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeInSeconds = (System.currentTimeMillis() - convertedDate.getTime()) / 1000;

        if (timeInSeconds < 60) {
            return "Vừa xong";
        } else if (timeInSeconds < 60 * 60) {
            return timeInSeconds / 60 + " phút trước";
        } else if (timeInSeconds < 60 * 60 * 24) {
            return timeInSeconds / (60 * 60) + " giờ trước";
        } else if (timeInSeconds < 60 * 60 * 24 * 30) {
            return timeInSeconds / (60 * 60 * 24) + " ngày trước";
        } else if (timeInSeconds < 60 * 60 * 24 * 30 * 12) {
            return timeInSeconds / (60 * 60 * 24 * 30) + " tháng trước";
        } else {
            return timeInSeconds / (60 * 60 * 24 * 30 * 12) + " năm trước";
        }
    }


}

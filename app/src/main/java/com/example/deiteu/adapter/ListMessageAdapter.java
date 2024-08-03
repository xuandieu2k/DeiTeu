package com.example.deiteu.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListMessageAdapter extends RecyclerView.Adapter<ListMessageAdapter.MyViewHolder> {

    private Context context;
    private List<Message> messageList;

    public ListMessageAdapter(Context context) {
        this.context = context;
        messageList =new ArrayList<>();
    }
    public void add(Message message)
    {
        messageList.add(message);
        notifyDataSetChanged();
    };
    public void addAll(List<Message> list)
    {
        messageList.addAll(list);
        notifyDataSetChanged();
    };
    public void clear()
    {
        messageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messgae_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getMessage() != null)
        {
            holder.message.setText(message.getMessage());
            holder.message.setVisibility(View.VISIBLE);
        }else{
            holder.message.setVisibility(View.GONE);
        }
        holder.timesend.setText(convertDatetime(message.getTimeSend()));
        Picasso.get().load(message.getImage()).into(holder.imgImage);
        if(message.getImage() != null)
        {
            holder.imgImage.setVisibility(View.VISIBLE);
        }else{
            holder.imgImage.setVisibility(View.GONE);
        }
        holder.message.setTextColor(context.getColor(R.color.white));
        if(message.getSenderId().equals(FirebaseAuth.getInstance().getUid()))
        {
//            holder.linearLayout.setGravity(Gravity.END);
            holder.linearLayout.setBackgroundResource(R.drawable.boxsend);
            holder.message.setGravity(Gravity.END);
            holder.timesend.setGravity(Gravity.END);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.END;
            holder.linearLayout.setLayoutParams(layoutParams);
        }else {
//            holder.linearLayout.setGravity(Gravity.START);
            holder.linearLayout.setBackgroundResource(R.drawable.boxreceiver);
            holder.message.setGravity(Gravity.START);
            holder.timesend.setGravity(Gravity.START);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.START;
            holder.linearLayout.setLayoutParams(layoutParams);

        }
        holder.imgImage.setOnClickListener(new View.OnClickListener() {
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
                Picasso.get().load(message.getImage()).into(img);
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
    private String convertDatetime(long timestamp)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String dateString = dateFormat.format(new Date(timestamp));
        return dateString;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView message,timesend;
        private ImageView imgImage;
        private LinearLayout linearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            message =itemView.findViewById(R.id.tvMessage);
            imgImage = itemView.findViewById(R.id.imgImage);
            timesend = itemView.findViewById(R.id.tvtimeSend);
            linearLayout = itemView.findViewById(R.id.messdetailslayout);
            imgImage.setVisibility(View.GONE);
        }
    }
}


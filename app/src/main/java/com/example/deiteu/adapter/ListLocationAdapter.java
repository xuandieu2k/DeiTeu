package com.example.deiteu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deiteu.R;
import com.example.deiteu.model.Location;
import com.example.deiteu.model.Message;

import java.util.ArrayList;
import java.util.List;

import io.woong.shapedimageview.RoundImageView;

public class ListLocationAdapter extends RecyclerView.Adapter<ListLocationAdapter.MyViewHolder> {

    private Context context;
    private List<Location> locationList;

    public ListLocationAdapter(Context context) {
        this.context = context;
        locationList = new ArrayList<>();
    }
    public void add(Location location)
    {
        locationList.add(location);
        notifyDataSetChanged();
    };
    public void addAll(List<Location> list)
    {
        locationList.addAll(list);
        notifyDataSetChanged();
    };
    public void clear()
    {
        locationList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messgae_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // CODE IS HERE
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private RoundImageView rounderimg;
        private TextView tv_title,tv_descrip,tv_address;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rounderimg = itemView.findViewById(R.id.rounderimg);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_descrip = itemView.findViewById(R.id.tv_descrip);
            tv_address = itemView.findViewById(R.id.tv_address);
        }
    }
}

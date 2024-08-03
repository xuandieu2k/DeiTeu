    package com.example.deiteu.adapter;

    import android.content.Context;
    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.ImageView;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.example.deiteu.R;
    import com.example.deiteu.activity.DetailsMessage;
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
    import java.util.Comparator;
    import java.util.List;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.MyViewHolder> {

        private Context context;
        private List<Users> usersList;

        public ListUserAdapter(Context context) {
            this.context = context;
            usersList =new ArrayList<>();
        }
        public void add(Users user)
        {
            usersList.add(user);
            notifyDataSetChanged();
        };

        public void addAll(List<Users> listU)
        {
            usersList.addAll(listU);
            notifyDataSetChanged();
        };
        public void removeObject(Users us)
        {
            usersList.remove(us);
            notifyDataSetChanged();
        }
        public void clear()
        {
            usersList.clear();
            notifyDataSetChanged();
        }
        public void replaceUser(List<Users> userList, Users newUser) {
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).equals(newUser)) {
                    userList.set(i, newUser);
                    break;
                }
            }
            notifyDataSetChanged();
        }
        public List<Users> getListUser()
        {
            return usersList;
        }
        public void sortTimesend()
        {
            Collections.sort(usersList, UsersLastMessageComparator);
        }
        public static Comparator<Users> UsersLastMessageComparator = new Comparator<Users>() {
            public int compare(Users user1, Users user2) {
                return Long.compare(user2.getFinalMessage().getTimeSend(), user1.getFinalMessage().getTimeSend());
            }
        };

        public List<Users> getUsersList() {
            return usersList;
        }

        public void setUsersList(List<Users> usersList) {
            this.usersList = usersList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_user,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Users us = usersList.get(position);
            if(us.getFullname() == null)
            {
                us.setFullname("Unknown name");
            }
            if(us.getFinalMessage() != null)
            {
                if(us.getFinalMessage().getSenderId().equals(FirebaseAuth.getInstance().getUid()))
                {
                    if(us.getFinalMessage().getMessage() != "" && us.getFinalMessage().getMessage() != null)
                    {
                        holder.email.setText("Bạn: "+us.getFinalMessage().getMessage());
                        holder.email.setTextColor(context.getResources().getColor(R.color.white));
                    }else{
                        if(us.getFinalMessage().getImage() != "" && us.getFinalMessage().getImage() != null)
                        {
                            holder.email.setText("Bạn: đã gửi một hình ảnh");
                            holder.email.setTextColor(context.getResources().getColor(R.color.white));
                        }else{
//                            holder.email.setText("Bạn: đã gửi một hình ảnh");
                        }
                    }
                }else{
                    if(us.getFinalMessage().getMessage() != "" && us.getFinalMessage().getMessage() != null)
                    {
                        if(!us.getFinalMessage().isReaded())
                        {
                            holder.email.setTextColor(context.getResources().getColor(R.color.green_custom));
                        }else{
                            holder.email.setTextColor(context.getResources().getColor(R.color.white));
                        }
                        holder.email.setText(us.getFinalMessage().getMessage());
                    }else{
                        if(us.getFinalMessage().getImage() != "" && us.getFinalMessage().getImage() != null)
                        {
                            if(!us.getFinalMessage().isReaded())
                            {
                                holder.email.setTextColor(context.getResources().getColor(R.color.green_custom));
                            }else{
                                holder.email.setTextColor(context.getResources().getColor(R.color.white));
                            }
                            holder.email.setText("Đã gửi một hình ảnh");
                        }else{
//                            holder.email.setText("Bạn: đã gửi một hình ảnh");
                        }
                    }
                }
            }else{
                holder.email.setText("Bạn chưa nhắn tin với người bạn này");
            }
            holder.name.setText(us.getFullname());
            if(us.getAvatar() != null)
            {
                Picasso.get().load(us.getAvatar()).into(holder.img_avatarchat);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(context, DetailsMessage.class);
                    it.putExtra("idReceiver",us.getId());
                    context.startActivity(it);
                }
            });
            //
            if(us.isOnline())
            {
                holder.img_active.setVisibility(View.VISIBLE);
            }else{
                holder.img_active.setVisibility(View.GONE);
            }
            holder.mUserDatabase.child(us.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users us = snapshot.getValue(Users.class);
                    if(!us.isOnline())
                    {
                        holder.img_active.setVisibility(View.GONE);
                    }else{
                        holder.img_active.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return usersList.size();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder{
            private final TextView name;
            private final TextView email;
            private ImageView img_active;

            public DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference("Users");

            private final CircleImageView img_avatarchat;
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txtUfullname);
                email =itemView.findViewById(R.id.txtUmessagefinal);
                img_avatarchat = itemView.findViewById(R.id.img_avatarchat);
                img_active = itemView.findViewById(R.id.img_active);
            }
        }
    }

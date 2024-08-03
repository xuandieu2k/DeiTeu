package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.adapter.TabPagerAdapter;
import com.example.deiteu.model.FirebaseHandler;
import com.example.deiteu.service.CallRoomService;
import com.example.deiteu.service.NotificationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;


public class Home2 extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    String idUser = "";

    private AppCompatButton btnlogout;
    private FirebaseAuth mAuth;
    private EditText txtText;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users").child(String.valueOf(FirebaseAuth.getInstance().getUid()));
    private boolean checkgender,checkage = false;

    private int backPressedCount = 0;
    @Override
    public void onBackPressed() {
        if (backPressedCount < 1) {
            Toast.makeText(this, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show();
            backPressedCount++;
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager.setUserInputEnabled(false); // Tắt chuyển tab bawng vuot
        TabPagerAdapter adapter = new TabPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText("Tab " + (position + 1));
                switch(position)
                {
                    case 0:
                        tab.setIcon(R.drawable.icon_findloveearth);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.icon_activitiescouple);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.icon_handmakelove);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.icon_chatlove);
                        break;
                    case 4:
                        tab.setIcon(R.drawable.icon_personal);
                        break;
                }
            }
        })
                .attach();
        // Set the first tab as selected by default
        TabLayout.Tab firstTab = tabLayout.getTabAt(0);
        if (firstTab != null) {
            firstTab.select();
        }
        FirebaseMessaging.getInstance()
                .subscribeToTopic("News").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       String msg = "Ngon";
                       if(!task.isSuccessful())
                       {
                           msg = "Lỗi";
                       }
                    }
                });

        Intent serviceIntent = new Intent(getApplicationContext(), CallRoomService.class);
        startService(serviceIntent);
        Intent serviceIntent1 = new Intent(getApplicationContext(), NotificationService.class);
        startService(serviceIntent1);
        idUser = FirebaseAuth.getInstance().getUid();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    FirebaseHandler firebaseHandler = new FirebaseHandler();
}
package com.example.deiteu.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.deiteu.R;
import com.example.deiteu.adapter.TabPagerAdapter;
import com.example.deiteu.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class contactFriend extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter adapter;
    ImageButton btnimg_back,btnimg_addfriend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_friend);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager.setUserInputEnabled(false); // Tắt chuyển tab bawng vuot
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);
        btnimg_back = findViewById(R.id.btnimg_back);
        btnimg_addfriend = findViewById(R.id.btnimg_addfriend);
        btnimg_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnimg_addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(contactFriend.this,addFriend.class);
                startActivity(it);
            }
        });
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
//                tab.setText("Tab " + (position + 1));
                switch(position)
                {
                    case 0:
                        tab.setText("Đang theo dõi");
                        break;
                    case 1:
                        tab.setText("Người theo dõi");
                        break;
                    case 2:
                        tab.setText("Yêu thích");
                        break;
                    case 3:
                        tab.setText("Sổ đen");
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

    }
}
package com.example.deiteu.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.deiteu.adapter.TabFragment;
import com.example.deiteu.fragment.ActivitiesCoupleFragment;
import com.example.deiteu.fragment.FindLoverFragment;
import com.example.deiteu.fragment.MessageFragment;
import com.example.deiteu.fragment.PostFragment;
import com.example.deiteu.fragment.UserSettingFragment;

public class TabPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 5;

    public TabPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
//        return TabFragment.newInstance(position);
        switch (position)
        {
            case 0: return new FindLoverFragment();
            case 1: return new ActivitiesCoupleFragment();
            case 2: return new PostFragment();
            case 3: return new MessageFragment();
            case 4: return new UserSettingFragment();
        }
        return TabFragment.newInstance(position);
    }


    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}

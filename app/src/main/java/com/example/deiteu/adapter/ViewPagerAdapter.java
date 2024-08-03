package com.example.deiteu.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.deiteu.R;
import com.example.deiteu.fragment.ActivitiesCoupleFragment;
import com.example.deiteu.fragment.BlackListFragment;
import com.example.deiteu.fragment.FindLoverFragment;
import com.example.deiteu.fragment.FollowingFragment;
import com.example.deiteu.fragment.IsFollowFragment;
import com.example.deiteu.fragment.LoveListFragment;
import com.example.deiteu.fragment.MessageFragment;
import com.example.deiteu.fragment.PostFragment;
import com.example.deiteu.fragment.UserSettingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final static int NUM_ITEMS = 4;
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new FollowingFragment();
            case 1: return new IsFollowFragment();
            case 2: return new LoveListFragment();
            case 3: return new BlackListFragment();
        }
        return ViewPagerFragmentt.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return NUM_ITEMS;
    }
}
class ViewPagerFragmentt extends Fragment {
    private static final String ARG_PAGE_NUMBER = "ARG_PAGE_NUMBER";
//    private int pageNumber;


    public static ViewPagerFragmentt newInstance(int pageNumber) {
        ViewPagerFragmentt fragment = new ViewPagerFragmentt();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}



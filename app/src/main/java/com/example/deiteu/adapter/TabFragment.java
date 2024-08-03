package com.example.deiteu.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import com.example.deiteu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TabFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "ARG_PAGE_NUMBER";
    private int pageNumber;


    public static TabFragment newInstance(int pageNumber) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        switch (pageNumber)
        {
            case 0: return inflater.inflate(R.layout.fragment_find_lover, container, false);
            case 1: return inflater.inflate(R.layout.fragment_activities_couple, container, false);
            case 2: return inflater.inflate(R.layout.fragment_post, container, false);
            case 3: return inflater.inflate(R.layout.fragment_message, container, false);
            case 4: return inflater.inflate(R.layout.fragment_user_setting, container, false);
            default:  return inflater.inflate(R.layout.fragment_tab, container, false);
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}


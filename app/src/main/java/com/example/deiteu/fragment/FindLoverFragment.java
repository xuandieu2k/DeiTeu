package com.example.deiteu.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.Blacklist;
import com.example.deiteu.model.FormatNumber;
import com.example.deiteu.adapter.UserCardAdapter;
import com.example.deiteu.model.SearchUtils;
import com.example.deiteu.model.Users;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import link.fls.swipestack.SwipeStack;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FindLoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindLoverFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppCompatButton buttonlogout;
    private FirebaseAuth mAuth;

    private DatabaseReference databaseReference;

    private DatabaseReference mUserDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
    private DatabaseReference mUser1Database = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Users");
    private SearchUtils searchUtils = new SearchUtils();
    //
    com.google.android.material.slider.RangeSlider range_slider;
    RelativeLayout maleLayout;
    RelativeLayout femaleLayout;
    RelativeLayout otherLayout;
    AppCompatButton apply_button;

    RadioButton radioMale;
    RadioButton radioFemale;
    RadioButton radioOther;
    private ImageButton btnimg_filter;

    private SwipeStack swipeStack;
    private UserCardAdapter adapter;
    private List<Users> userList;
    private List<String> idStringList;
    TextView tv_change_age,tv_gender;

    private SharedPreferences sharedPreferences;
    public DatabaseReference mBlacklistDatabase = FirebaseDatabase.getInstance("https://deiteu-0905-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Blacklist");

    private FormatNumber formatNumber;
    public FindLoverFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindLoverFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindLoverFragment newInstance(String param1, String param2) {
        FindLoverFragment fragment = new FindLoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_lover, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        btnimg_filter = view.findViewById(R.id.btnimg_filter);
        //
        idStringList = new ArrayList<>();
         // Lấy instance của SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("save_filter", Context.MODE_PRIVATE);
        // Lưu trữ dữ liệu
        SharedPreferences.Editor editor = sharedPreferences.edit();
        userList = new ArrayList<>();
        swipeStack = view.findViewById(R.id.swipeStack);
        adapter = new UserCardAdapter(userList, getContext());
        swipeStack.setAdapter(adapter);

        swipeStack.setListener(new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {

            }

            @Override
            public void onViewSwipedToRight(int position) {

            }


            @Override
            public void onStackEmpty() {
                LoadData();
            }
        });
        // add users to userList
        //
        getListU();
        LoadData();

        btnimg_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
                View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_filter_findlover, null);
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                range_slider = bottomSheetView.findViewById(R.id.range_slider);
                apply_button = bottomSheetView.findViewById(R.id.apply_button);

                tv_gender = bottomSheetView.findViewById(R.id.tv_gender);
                tv_change_age = bottomSheetView.findViewById(R.id.tv_change_age);
                // Lấy giá trị
                String min =sharedPreferences.getString("min_value", "17");
                String max =  sharedPreferences.getString("max_value", "40");
                if(min.equals(""))
                {
                    min = "0";
                }
                if(max.equals(""))
                {
                    max = "0";
                }
                minvalue = Float.parseFloat(min);
                maxvalue = Float.parseFloat(max);
                setTextAge();

                String gender = sharedPreferences.getString("gender", "2");
                setTextGender(gender);

                //
                range_slider.addOnChangeListener(new RangeSlider.OnChangeListener() {
                    @Override
                    public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                        List<Float> values = slider.getValues();
                        float min = values.get(0);
                        float max = values.get(1);
                        minvalue = (int) min;
                        maxvalue = (int) max;
                        setTextAge();
                    }
                });
                if(minvalue > 0 && maxvalue > 0)
                {
                    range_slider.setValues((float)minvalue, (float)maxvalue);
                    setTextAge();
                }
                apply_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(checkgender.equals(""))
                        {
                            editor.putString("gender", "0");
                            editor.apply();
                        }else{
                            editor.putString("gender", checkgender);
                            editor.apply();
                        }
                        if(minvalue > 0)
                        {
                            editor.putString("min_value", String.valueOf(minvalue));
                            editor.apply();
                        }
                        if(maxvalue > 0)
                        {
                            editor.putString("max_value",  String.valueOf(maxvalue));
                            editor.apply();
                        }
                        setTextAge();
                        setTextGender(checkgender);
                        adapter.clear();
                        LoadData();
                        Toast.makeText(getContext(), "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.cancel();
                    }
                });
                // Ánh xạ view
                maleLayout = bottomSheetView.findViewById(R.id.male_layout);
                femaleLayout = bottomSheetView.findViewById(R.id.female_layout);
                otherLayout = bottomSheetView.findViewById(R.id.other_layout);

                radioMale = bottomSheetView.findViewById(R.id.radio_male);
                radioFemale = bottomSheetView.findViewById(R.id.radio_female);
                radioOther = bottomSheetView.findViewById(R.id.radio_twogender);
                switch (gender)
                {
                    case "0":
                        radioMale.setChecked(false);
                        radioFemale.setChecked(false);
                        radioOther.setChecked(true);
                        otherLayout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                        break;
                    case "1":
                        radioMale.setChecked(true);
                        radioFemale.setChecked(false);
                        radioOther.setChecked(false);
                        maleLayout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                        break;
                    case "2":
                        radioMale.setChecked(false);
                        radioFemale.setChecked(true);
                        radioOther.setChecked(false);
                        femaleLayout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                        break;
                }

                // Set sự kiện cho RadioButton
                radioMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            maleLayout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                            radioFemale.setChecked(false);
                            radioOther.setChecked(false);
                            checkgender = "1";
                            setTextGender(checkgender);
                        } else {
                            maleLayout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                        }
                    }
                });

                radioFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            femaleLayout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                            radioMale.setChecked(false);
                            radioOther.setChecked(false);
                            checkgender = "2";
                            setTextGender(checkgender);
                        } else {
                            femaleLayout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                        }
                    }
                });

                radioOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            otherLayout.setBackground(getResources().getDrawable(R.drawable.box_selected_gender));
                            radioMale.setChecked(false);
                            radioFemale.setChecked(false);
                            checkgender = "0";
                            setTextGender(checkgender);
                        } else {
                            otherLayout.setBackground(getResources().getDrawable(R.drawable.box_noneselect_gender));
                        }
                    }
                });
                //
            }
        });
    }
    private String checkgender = "";
    public void setTextAge()
    {
        tv_change_age.setText((int)minvalue+" - "+(int)maxvalue);
    }
    public void setTextGender(String gender)
    {
        switch (gender) {
            case "1": tv_gender.setText("Nam"); break;
            case "2": tv_gender.setText("Nữ"); break;
            case "0": tv_gender.setText("LGBT"); break;
            default:
        }

    }
    private float maxvalue,minvalue = 0;
    public void getListU()
    {
        idStringList.clear();
        String idU = FirebaseAuth.getInstance().getUid();
        assert idU != null;
        mBlacklistDatabase.child(idU).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot data : snapshot.getChildren())
                    {
                        Blacklist follows = data.getValue(Blacklist.class);
                        idStringList.add(follows.getIdUser());
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void LoadData()
    {
        String max = sharedPreferences.getString("max_value","40");
        String min = sharedPreferences.getString("min_value","17");
        float maxAge = Float.parseFloat(max);
        float minAge = Float.parseFloat(min);
        String gender = sharedPreferences.getString("gender","2");
        //
        mUserDatabase.keepSynced(false);
        mUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    for (DataSnapshot data: snapshot.getChildren())
                    {
                        Users us = data.getValue(Users.class);
                        String birthday = "";
                        birthday= us.getBirthday();
                        if(birthday != null)
                        {
                            if (!birthday.equals("") && !birthday.equals("?? / ?? / ????"))
                            {
                                int age = formatNumber.calculateAge(Integer.parseInt(birthday.substring(6, birthday.length())), Integer.parseInt(birthday.substring(3, 5)), Integer.parseInt(birthday.substring(0, 2)));
                                if(!us.getId().equals(FirebaseAuth.getInstance().getUid()))
                                {
                                    if(!gender.equals("")) // TH1
                                    {
                                        if(us.getGender() != null)
                                        {
                                            switch (gender)
                                            {
                                                case "1":
                                                case "2":
                                                case "0":
                                                    if(gender.equals(us.getGender()) && (age <= maxAge) && (minAge <= age))
                                                    {
                                                        if(!idStringList.contains(us.getId()))
                                                        {
                                                            adapter.add(us);
                                                        }
                                                    }
                                                    break;
                                            }
                                        }else{
                                            // user not update infor
                                        }

                                    }else{  /// TH2
                                        if((age <= maxAge) && (minAge <= age))
                                        {
                                            if(!idStringList.contains(us.getId()))
                                            {
                                                adapter.add(us);
                                            }
                                        }
                                    }
                                }
                            } //
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
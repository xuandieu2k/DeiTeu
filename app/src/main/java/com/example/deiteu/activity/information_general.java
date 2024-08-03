package com.example.deiteu.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.model.FirebaseHandler;
import com.google.firebase.auth.FirebaseAuth;

public class information_general extends AppCompatActivity {
    private AppCompatButton btnlogout;
    private ImageButton button_backgeneral;
    private AppCompatButton btn_update,btn_language,btn_feedback,btn_termofuse,btn_aboutus;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_general);
        btnlogout = findViewById(R.id.button_logout);
        mAuth = FirebaseAuth.getInstance();
        button_backgeneral = findViewById(R.id.button_backgeneral);
        btn_update = findViewById(R.id.button_editpersonal);
        btn_feedback = findViewById(R.id.button_feedback);
        btn_language= findViewById(R.id.button_changelanguage);
        btn_termofuse = findViewById(R.id.button_terms_of_use);
        btn_aboutus = findViewById(R.id.button_about_us);
        btn_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(information_general.this, "Hiện tại Deiteu chỉ hỗ trợ ngôn ngữ tiếng việt", Toast.LENGTH_SHORT).show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(information_general.this,edit_infor_user.class);
                startActivity(it);
            }
        });
        button_backgeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uId = FirebaseAuth.getInstance().getUid();
                mAuth.signOut();
                firebaseHandler.Logout(uId);
                Intent it  = new Intent(information_general.this, Login.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);
            }
        });
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(information_general.this,feedback.class);
                startActivity(it);
            }
        });
    }
    private FirebaseHandler firebaseHandler = new FirebaseHandler();
}
package com.example.deiteu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.deiteu.R;
import com.example.deiteu.service.BootReceiver;
import com.example.deiteu.service.CallRoomService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    //    private NetworkChangeReceiver networkChangeReceiver;
    private static final int DELAY_TIME = 2000; //5s
    private boolean isConnected;

    private FirebaseAuth mAuth;

    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        networkChangeReceiver = new NetworkChangeReceiver();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        // Sử dụng Handler và Runnable để chuyển đổi sang activity mới
        // Kiểm tra kết nối internet
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnected();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUser != null) {
                    Intent intent = new Intent(MainActivity.this, Home2.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Người dùng chưa đăng nhập, yêu cầu đăng nhập hoặc chuyển đến màn hình đăng nhập
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, DELAY_TIME);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isConnected) {
//                    if (mUser != null) {
//                        // Người dùng đã đăng nhập trước đó, thực hiện các tác vụ cần thiết ở đây
//                        Intent intent = new Intent(MainActivity.this, Home2.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        // Người dùng chưa đăng nhập, yêu cầu đăng nhập hoặc chuyển đến màn hình đăng nhập
//                        Intent intent = new Intent(MainActivity.this, Login.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "Không có kết nối internet", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//        }, DELAY_TIME);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        // Đăng ký nhận sự kiện thay đổi kết nối mạng
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(networkChangeReceiver, filter);
//
//        // Kiểm tra trạng thái kết nối mạng
//        checkNetworkStatus();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // Hủy đăng ký nhận sự kiện thay đổi kết nối mạng
//        unregisterReceiver(networkChangeReceiver);
//    }
//
//    private void checkNetworkStatus() {
//        if (isNetworkConnected()) {
//            Intent intent = new Intent(this, Login.class);
//            startActivity(intent);
//            finish(); // Đóng activity hiện tại
//        } else {
//            Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private boolean isNetworkConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.isConnected();
//    }
//
//    private class NetworkChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//                boolean isConnected = isNetworkConnected(context);
//                if (isConnected) {
//                    checkNetworkStatus();
//                }
//            }
//        }
//
//        private boolean isNetworkConnected(Context context) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            return networkInfo != null && networkInfo.isConnected();
//        }
//    }
}
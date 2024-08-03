package com.example.deiteu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Khởi động service tại đây
            Intent serviceIntent = new Intent(context, CallRoomService.class);
            context.startService(serviceIntent);
        }
    }
}
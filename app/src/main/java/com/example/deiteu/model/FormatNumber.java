package com.example.deiteu.model;

import android.annotation.SuppressLint;
import android.icu.util.Calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FormatNumber {
    @SuppressLint("DefaultLocale")
    public String formatFollowersCount(int count) {
        if (count >= 1000000000) {
            return String.format("%.1fG", (float) count / 1000000000);
        } else if (count >= 1000000) {
            return String.format("%.1fM", (float) count / 1000000);
        } else if (count >= 1000) {
            return String.format("%.1fK", (float) count / 1000);
        } else {
            return String.valueOf(count);
        }
    }
    public String convertLongTimetoDateTime(long timestamp)
    {
        Date date = new Date(timestamp);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }
//    public static int calculateAge(int year, int month, int day) {
//        Calendar birthdate = Calendar.getInstance();
//        Calendar today = Calendar.getInstance();
//        birthdate.set(year, month, day);
//        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
//        if (today.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)){
//            age--;
//        }
//        return age;
//    }
    public static int calculateAge(int year, int month, int day) {
        Calendar birthdate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        birthdate.set(year, month - 1, day); // Trừ 1 từ tháng vì Calendar.MONTH bắt đầu từ 0 (tháng 0 = tháng 1)
        int age = today.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);

        // Kiểm tra nếu ngày sinh đã vượt qua ngày hiện tại trong năm hiện tại, thì giảm tuổi đi 1
        if (today.get(Calendar.MONTH) < birthdate.get(Calendar.MONTH)
                || (today.get(Calendar.MONTH) == birthdate.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) < birthdate.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        return age;
    }


    public String getTimeCurrent() {
        LocalDateTime dateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            dateTime = LocalDateTime.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        }
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formattedDateTime = dateTime.format(formatter);
        }
        return formattedDateTime;
    }
    public static long convertToMiliSecond(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        try {
            Date date = format.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getTimeAgo(long timeInMillis) {
        final long now = System.currentTimeMillis();
        final long secondsAgo = (now - timeInMillis) / 1000;

        if (secondsAgo < 60) {
            return "Vừa xong";
        } else if (secondsAgo < 60 * 60) {
            long minutesAgo = secondsAgo / 60;
            return minutesAgo + " phút trước";
        } else if (secondsAgo < 24 * 60 * 60) {
            long hoursAgo = secondsAgo / (60 * 60);
            return hoursAgo + " giờ trước";
        } else if (secondsAgo < 48 * 60 * 60) {
            return "Hôm qua";
        } else {
            SimpleDateFormat simpleDateFormat;
            if (now - timeInMillis > 365 * 24 * 60 * 60 * 1000L) {
                simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            } else if (now - timeInMillis > 24 * 60 * 60 * 1000L) {
                simpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
            } else {
                simpleDateFormat = new SimpleDateFormat("HH:mm");
            }
            return simpleDateFormat.format(new Date(timeInMillis));
        }
    }


}

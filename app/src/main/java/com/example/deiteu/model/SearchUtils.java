package com.example.deiteu.model;

import android.media.MediaRecorder;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SearchUtils {
    public List<Users> searchUsers(List<Users> userList, String searchQuery) {
        List<Users> result = new ArrayList<>();
        for (Users user : userList) {
            String fullname = user.getFullname();
            String id = user.getId();
            if ((fullname != null && removeAccent(fullname.toLowerCase()).contains(removeAccent(searchQuery.toLowerCase()))) || (id.equals(searchQuery))) {
                result.add(user);
            }
        }
        return result;
    }

    private String removeAccent(String input) {
        String result = input.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        result = result.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        result = result.replaceAll("[ìíịỉĩ]", "i");
        result = result.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        result = result.replaceAll("[ùúụủũưừứựửữ]", "u");
        result = result.replaceAll("[ỳýỵỷỹ]", "y");
        result = result.replaceAll("đ", "d");
        return result;
    }
}
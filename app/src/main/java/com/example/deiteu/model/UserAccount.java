package com.example.deiteu.model;

import android.os.AsyncTask;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.text.WordUtils;

public class UserAccount {
    List<Users> listUser = new ArrayList<>();
    private static final String[] MALE_FIRST_NAMES = {"Trung", "Nam", "An","Đức","Long","Hòa","Hưng","Anh", "Bình", "Huy", "Đạt", "Duy", "Quang", "Việt"};
    private static final String[] FEMALE_FIRST_NAMES = {"Thu", "Linh", "Hương", "Thảo", "Dung","Mỹ", "Hà","Ngọc","Linh","Hiền", "Mai", "Lan", "Ngọc"};
    private static final String[] LAST_NAMES = {"Nguyễn", "Trần", "Lê", "Phạm", "Huỳnh", "Võ", "Đặng", "Bùi", "Đỗ"};
    private static final String[] EMAIL_PROVIDERS = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com"};
    private static final String UNSPLASH_URL = "https://unsplash.com/fr/s/photos/beautiful-face-vietnam";
    private static Random random = new Random();
    public List<Users> GenerateList(int num)
    {
        List<String> listurl = readImageUrls();
        for (int i=0;i<num;i++)
        {
            Users us = new Users();
            String gender = getRandomGender();
            String firstName = getRandomFirstName(gender);
            String lastName = getRandomLastName();
            LocalDate birthDate = getRandomBirthDate();
            LocalDate localDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                localDate = LocalDate.parse(String.valueOf(birthDate));
            }
            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            }
            String formattedDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formattedDate = localDate.format(formatter);
            }
            String email = getRandomEmail(firstName, lastName);
            String normalizedEmail = Normalizer.normalize(email, Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            String imageUrl = getRandomImageUrl(listurl);
            us.setFullname(WordUtils.capitalizeFully(firstName + " " + lastName));
            us.setAvatar(imageUrl);
            us.setGender(gender);
            us.setEmail(normalizedEmail);
            us.setBirthday(formattedDate);
            listUser.add(us);
        }
        return listUser;
    }


    private static String getRandomGender() {
        List<String> genders = Arrays.asList("1", "2","3");
        return genders.get(random.nextInt(genders.size()));
    }

    private static String getRandomFirstName(String gender) {
        String[] firstNames = (gender.equals("1")) ? MALE_FIRST_NAMES : FEMALE_FIRST_NAMES;
        return firstNames[random.nextInt(firstNames.length)];
    }

    private static String getRandomLastName() {
        return LAST_NAMES[random.nextInt(LAST_NAMES.length)];
    }

    private static LocalDate getRandomBirthDate() {
        int year = random.nextInt(20) + 1980;
        int month = random.nextInt(12) + 1;
        int day = random.nextInt(28) + 1; // Assume all months have max of 28 days
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return LocalDate.of(year, month, day);
        }
        return null;
    }

    private static String getRandomEmail(String firstName, String lastName) {
        String username = firstName.toLowerCase() + lastName.toLowerCase() + random.nextInt(100);
        String provider = EMAIL_PROVIDERS[random.nextInt(EMAIL_PROVIDERS.length)];
        return username + "@" + provider;
    }
    public List<String> readImageUrls() {
        String directoryPath = "C:\\Users\\Dell\\OneDrive - hcmunre.edu.vn\\Desktop\\AppANDROID\\DeiTeu\\app\\src\\main\\java\\com\\example\\deiteu\\Image";
        File directory = new File(directoryPath);
        List<String> imageUrls = new ArrayList<>();
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif")) {
                            imageUrls.add(file.getAbsolutePath());
                        }
                    }
                }
            }
        }
        return imageUrls;
    }
    public List<String> readList()
    {
        // Đường dẫn đến thư mục chứa hình ảnh
        String folderPath = "C:\\Image";

        // Tạo một đối tượng File để đại diện cho thư mục chứa hình ảnh
        File folder = new File(folderPath);

        // Tạo một danh sách để lưu các URL hình ảnh
        List<String> imageUrls = new ArrayList<>();

        // Lấy danh sách các tệp trong thư mục
        File[] files = folder.listFiles();

        // Lặp qua danh sách các tệp
        for (File file : files) {
            // Nếu tệp là một tệp hình ảnh
            if (isImageFile(file)) {
                // Lấy đường dẫn tuyệt đối của tệp và thêm nó vào danh sách các URL hình ảnh
                imageUrls.add(file.getAbsolutePath());
            }
        }
        return imageUrls;
    }
    // Phương thức kiểm tra xem tệp có phải là tệp hình ảnh không
    private static boolean isImageFile(File file) {
        String name = file.getName();
        String extension = name.substring(name.lastIndexOf(".") + 1);
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "bmp"};
        for (String ext : imageExtensions) {
            if (extension.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }
    public static String getRandomImageUrl(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return null;
        }
        String imageUrl;
        do {
            imageUrl = imageUrls.get(random.nextInt(imageUrls.size()));
        } while (!isValidImageUrl(imageUrl));
        return imageUrl;
    }

    private static boolean isValidImageUrl(String imageUrl) {
        // Add your own image validation logic here
        // For example, check if the image URL returns a valid image file
        return true;
    }







}
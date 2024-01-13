package com.buaa.food;

public class UserAuth {
    private static String userPhone = null;

    public static void setLocalUserPhone(String phone) {
        userPhone = phone;
    }

    public static String getLocalUserPhone() {
        return userPhone;
    }

    public static boolean isUserLoggedIn() {
        return userPhone != null;
    }

    public static void logout() {
        userPhone = null;
    }

}

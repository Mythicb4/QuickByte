package com.mycompany.quickbite.util;

public class AppState {
    private static String userType;

    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String type) {
        userType = type;
    }
}

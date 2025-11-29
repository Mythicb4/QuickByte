package com.mycompany.quickbite.util;

public class AppState {
    private static String userType;
    private static String userEmail;
    
    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String type) {
        userType = type;
    }
    public static void setUserEmail(String email) {
        userEmail = email;
    }
    public static String getUserEmail(){
        return userEmail;
    }
}

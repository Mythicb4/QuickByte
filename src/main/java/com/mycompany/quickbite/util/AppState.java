package com.mycompany.quickbite.util;

import com.mycompany.quickbite.model.Business;

public class AppState {
    private static String userType;
    private static String userEmail;
    private static Business selectedBusiness;

    public static String getUserType() {
        return userType;
    }

    public static void setUserType(String type) {
        userType = type;
    }

    public static void setUserEmail(String email) {
        userEmail = email;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static Business getSelectedBusiness() {
        return selectedBusiness;
    }

    public static void setSelectedBusiness(Business business) {
        selectedBusiness = business;
    }
}

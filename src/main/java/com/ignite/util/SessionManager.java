package com.ignite.util;

import com.ignite.model.User;
import com.ignite.model.Admin;

public class SessionManager {

    private static User currentUser;
    private static Admin currentAdmin;

    // ---------------- USER SESSION ---------------- //

    public static void setCurrentUser(User user) {
        currentUser = user;
        currentAdmin = null; // prevent overlap
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }


    // ---------------- ADMIN SESSION ---------------- //

    public static void setCurrentAdmin(Admin admin) {
        currentAdmin = admin;
        currentUser = null; // prevent overlap
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }


    // ---------------- LOGOUT ---------------- //

    public static void logout() {
        currentUser = null;
        currentAdmin = null;
    }

    public static int getCurrentUserId() {
        if (currentUser != null) {
            return currentUser.getUserId();
        }
        if (currentAdmin != null) {
            return currentAdmin.getAdminId(); // or getId() depending on your model
        }
        return -1;
    }

}

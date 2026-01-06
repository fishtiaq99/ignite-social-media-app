package com.ignite.service;

import com.ignite.model.Admin;
import com.ignite.model.User;

public class AuthService {

    private UserService userService;
    private AdminService adminService;

    private static User currentUser;
    private static Admin currentAdmin;

    public AuthService() {
        this.userService = new UserService();
        this.adminService = new AdminService();
    }

    // student login
    public User login(String username, String password) {
        User user = userService.login(username, password);
        currentUser = user;
        currentAdmin = null;
        return user;
    }

    // admin login
    public Admin loginAdmin(String username, String password) {
        Admin admin = adminService.login(username, password);
        currentAdmin = admin;
        currentUser = null;
        return admin;
    }

    // logout clears both
    public void logout() {
        currentUser = null;
        currentAdmin = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    public static boolean isUserLoggedIn() {
        return currentUser != null;
    }
}

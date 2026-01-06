package com.ignite.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordUtil {

    // No hashing at all
    public static String hashPassword(String password) {
        return password; // store as raw text
    }

    // Plain string comparison
    public static boolean verify(String inputPassword, String storedPassword) {
        return inputPassword.equals(storedPassword);
    }

}
package com.bci.challenge.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    /**
     * (?=[^A-Z]*[A-Z][^A-Z]*$) verifies that's only one uppercase letter.
     * (?=(?:\D*\d){2}\D*$) verifies that there are only two digits, not necessarily adjacent.
     * [a-zA-Z\d]{8,12}$ verifies that the password has between 8 and 12 characters, only letters or digits.
     */
    private static final String PASSWORD_REGEX = "^(?=[^A-Z]*[A-Z][^A-Z]*$)(?=(?:\\D*\\d){2}\\D*$)[a-zA-Z\\d]{8,12}$";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}


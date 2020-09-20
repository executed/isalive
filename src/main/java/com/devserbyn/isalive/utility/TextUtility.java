package com.devserbyn.isalive.utility;

public class TextUtility {

    public static boolean isNumber(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

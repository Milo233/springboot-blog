package com.yuan.blog.util;

public class StringUtils {
    public static boolean isEmpty(String ... str){
        if (str == null || str.length == 0){
            return true;
        }
        for (String s : str){
            if (s == null || s.trim().length() == 0){
                return true;
            }
        }
        return false;
    }
}

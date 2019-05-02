package com.yuan.blog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	// 从字符串提取整数
    public static String getNumber(String str){
        if (isEmpty(str)) return "";

        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}

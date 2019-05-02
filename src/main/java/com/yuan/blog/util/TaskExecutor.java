package com.yuan.blog.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TaskExecutor {


    public static String exec(String command) {
        StringBuilder result = new StringBuilder();
        try {
            String s;
            Process p;
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null) {
                result.append(s);
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            result = new StringBuilder(e.getMessage());
        }
        return result.toString();
    }
}

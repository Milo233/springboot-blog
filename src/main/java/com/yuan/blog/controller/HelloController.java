package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.util.NetUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/run")
public class HelloController {

    @GetMapping("/reboot")
    public String hello() {
        StringBuilder result = new StringBuilder();
        User currentUser = NetUtil.getCurrentUser();
        // 指定用户才能重启
        if(currentUser == null || !"milo".equals(currentUser.getUsername())){
            return "invalid action!";
        }
        String os = System.getProperty("os.name");
        if (!os.equalsIgnoreCase("Linux")) {
            return "not linux！！！";
        }

        try {
            String s;
            Process p;
            p = Runtime.getRuntime().exec("sh /root/rebootBlog.sh");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null){
                result.append(s);
                System.out.println("line: " + s);
            }
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
            result = new StringBuilder(e.getMessage());
        }
        return result.toString();
    }
}

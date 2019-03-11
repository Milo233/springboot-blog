package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.service.BlogService;
import com.yuan.blog.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Controller
@RequestMapping("/run")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    private static final String REBOOT_COMMAND = "sh /root/rebootBlog.sh";

    @Autowired
    private BlogService blogService;

    @GetMapping("/{command}")
    public String hello(@PathVariable("command") String command) {
        User currentUser = NetUtil.getCurrentUser();
        // 指定用户才能重启
        if(currentUser == null || !"milo".equals(currentUser.getUsername())){
            return "invalid action!";
        }
        log.info(currentUser.getUsername() + " is trying to execute " + command);
        String os = System.getProperty("os.name");
        if (!os.equalsIgnoreCase("Linux")) {
            return "not linux！！！";
        }

        if("reboot".equals(command)){
            return exec(REBOOT_COMMAND);
        } else {
            return exec(command);
        }
    }

    @GetMapping("/update/{content}")
    public ModelAndView update(@PathVariable("content") String content) {
        log.info("aws update content " + content);
        if(content == null || content.length() == 0 || content.length() > 200){
            return new ModelAndView("copy", "userModel", "failed!!");
        }
        // 修改表数据
        blogService.updateWord(content,1);
        return new ModelAndView("copy", "userModel", content);
    }

    @GetMapping("/query/{id}")
    public ModelAndView query(@PathVariable("id") Integer id) {
        String content = blogService.getContentById(id);
        return new ModelAndView("copy", "userModel", content);
    }

    public String exec(String command){
        StringBuilder result = new StringBuilder();
        try {
            String s;
            Process p;
            p = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null){
                result.append(s);
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            result = new StringBuilder(e.getMessage());
        }
        return  result.toString();
    }
}

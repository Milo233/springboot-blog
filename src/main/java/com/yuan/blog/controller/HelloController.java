package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.service.BlogService;
import com.yuan.blog.util.NetUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/run")
public class HelloController {

    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    final org.apache.logging.log4j.Logger logger = LogManager.getLogger();

    private static final String REBOOT_COMMAND = "sh /root/rebootBlog.sh";

    @Autowired
    private BlogService blogService;

    @GetMapping("/query/{id}")
    @ResponseBody
    public String query(@PathVariable("id") Integer id) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        logger.error("error method" + sdf.format(date));
        logger.info("info method" + sdf.format(date));
        logger.warn("warn method " + sdf.format(date));
        // 某一个日志级别会打印比它 intValue 低的级别的日志.所以自定义的日志级别的value要比 常见的error info低
        // OFF < FATAL < ERROR < WARN < INFO < DEBUG < TRACE < ALL
        // 自定义日志级别
        logger.log(Level.forName("DIAG", 100), "another message");
        return blogService.getContentById(id);
    }

    @GetMapping("/{command}")
    @ResponseBody
    public String hello(@PathVariable("command") String command) {
        User currentUser = NetUtil.getCurrentUser();
        // 指定用户才能重启
        if (currentUser == null || !"milo".equals(currentUser.getUsername())) {
            log.error("invalid action");
            return "invalid action";
        }
        log.info(currentUser.getUsername() + " is trying to execute " + command);
        String os = System.getProperty("os.name");
        if (!os.equalsIgnoreCase("Linux")) {
            log.error("not linux");
            return "not linux";
        }

        if ("reboot".equals(command)) {
            return exec(REBOOT_COMMAND);
        } else {
            return exec(command);
        }
    }

    @GetMapping("/update/{content}")
    @ResponseBody
    public String update(@PathVariable("content") String content) {
        log.info("aws update content " + content);
        if (content == null || content.length() == 0 || content.length() > 200) {
            return "failed!!";
        }
        // 修改表数据
        blogService.updateWord(content, 1);
        return "done!!";
    }

    public String exec(String command) {
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

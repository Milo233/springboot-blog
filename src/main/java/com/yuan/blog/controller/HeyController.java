package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.util.NetUtil;
import com.yuan.blog.util.TaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/run")
public class HeyController {
    private static final Logger log = LoggerFactory.getLogger(HeyController.class);
    private static final String REBOOT_COMMAND = "sh /root/rebootBlog.sh";

    @GetMapping("/command/{command}")
    @ResponseBody
    public String hello(@PathVariable("command") String command) {
        User currentUser = NetUtil.getCurrentUser(false);
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
            return TaskExecutor.exec(REBOOT_COMMAND);
        } else {
            return "";
//            return TaskExecutor.exec(command);
        }
    }
}

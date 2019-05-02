package com.yuan.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/run2")
public class HelloController {
    /*private static final Logger log = LoggerFactory.getLogger(HelloController.class);
    final org.apache.logging.log4j.Logger logger = LogManager.getLogger();
    private static final String REBOOT_COMMAND = "sh /root/rebootBlog.sh";

    @Autowired
    public JavaMailSender mailSender;

    @Autowired
    private BlogService blogService;
    @Value("${spring.mail.username}")
    private String username;

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

    @GetMapping("/command/{command}")
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

        log.info("start to send mail");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo("1611756376@qq.com");
            message.setSubject("主题：简单邮件");
            message.setText("测试邮件内容");
            mailSender.send(message);
        } catch (Exception e){
            e.printStackTrace();
            log.error("发送邮件失败 " + e);
        }
        log.info("发送邮件成功");

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
    }*/
}

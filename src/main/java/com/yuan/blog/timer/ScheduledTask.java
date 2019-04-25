package com.yuan.blog.timer;

import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.service.SystemLogService;
import com.yuan.blog.service.TodoService;
import com.yuan.blog.util.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@EnableScheduling
@Component
public class ScheduledTask {

    @Resource
    private SystemLogService systemLogService;
    // 必须放在spring管理的类里才能正常注入JavaMailSender，否则就是null。怀疑是类实例化的时间先后有关
    @Autowired
    private TodoService todoService;
    @Autowired
    public JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String username;


    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    //@Scheduled(fixedRate = 4 * 60 * 60 * 1000 ) // 24h 一次 项目第一次启动时就会执行 大部分时候启动时会执行两次。。还是用cron的好
    @Scheduled(cron = "0 53 22 * * ?")
    public void deleteSystemLog() {
        log.info("cron 定时任务 删除3天以前的日志(留下id小于、等于50的)");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        systemLogService.deleteLogsBefore(cal.getTime());
    }

    // 每10分钟移除一次过期的缓存
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void removeExpireCache() {
        int count = Cache.removeExpireCache();
        if (count > 0) {
            log.info("定时任务 移除 缓存数据数量：" + count);
        }
    }

//    @Scheduled(fixedRate = 1 * 60 * 1000)
    @Scheduled(cron = "0 0 20 * * ?")
    public void todoNotify() {
        // 查询需要通知的 todo 这里要聚合一下 同一个用户，发一条邮件
        List<TodoResponse> todoList = todoService.queryForNotify();
        if (todoList != null && todoList.size() > 0){
            for (TodoResponse todo : todoList){
                sendMail(todo.getEmail(), todo.getContent() + new Date().toString(), "延期啦！！");
            }
        }
        Integer count = 10;
        log.info("定时任务 发送提醒邮件：" + "count");
    }

    public void sendMail(String to,String text,String subject) {
        long start = System.currentTimeMillis();
        // 纯文本邮件。支持html or 附件需要MimeMessage
        // todo https://blog.csdn.net/larger5/article/details/80534887
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(to); // 收件人
            message.setSubject(subject);//主题
            message.setText(text); // 正文
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
            log.error("failed to sendmail " + e);
        }
        log.info("sendmail cost time " + (System.currentTimeMillis() - start) + " ms");
    }
}

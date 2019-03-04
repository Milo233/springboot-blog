package com.yuan.blog.timer;

import com.yuan.blog.service.SystemLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class ScheduledTask {

    @Autowired
    private SystemLogService systemLogService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    @Scheduled(cron="0 30 /6 * * ? ") // 每6个小时 执行一次
//    @Scheduled(fixedRate = 4 * 60 * 60 * 1000 ) // 24h 一次 项目第一次启动时就会执行 大部分时候启动时会执行两次。。还是用cron的好
    public void deleteSystemLog() {
        log.info("cron 定时任务 每天执行4一次 删除3天以前的日志(留下id小于、等于50的)");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-3);
        systemLogService.deleteLogsBefore(cal.getTime());
    }
}

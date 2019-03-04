package com.yuan.blog.timer;

import com.yuan.blog.service.SystemLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@EnableScheduling
@Component
public class ScheduledTask {

    @Autowired
    private SystemLogService systemLogService;

    private Date LAST_DATE = null;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

//    @Scheduled(cron="0 0 * * *  ?") // 每小时 整点 0分执行一次
    @Scheduled(fixedRate = 4 * 60 * 60 * 1000 ) // 24h 一次 项目第一次启动时就会执行一次
    public void deleteSystemLog() {
        // todo 有时刚启动项目时会执行两次。。
        if (LAST_DATE != null && (new Date().getTime() - LAST_DATE.getTime() < 4 * 60 * 60 * 1000)){
            log.info("定时重复啦！！！");
        }
        log.info("定时任务 每天执行一次 删除3天以前的日志(留下id小于、等于50的)");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-3);
        systemLogService.deleteLogsBefore(cal.getTime());
    }
}

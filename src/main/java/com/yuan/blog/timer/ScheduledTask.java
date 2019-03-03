package com.yuan.blog.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class ScheduledTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    @Scheduled(cron="0 0 * * *  ?") // 每小时 整点 0分执行一次
//    @Scheduled(fixedRate = 4 * 60 * 60 * 1000 ) // 4h 一次 项目第一次启动时就会执行一次
    public void testOne() {
        log.info("定时任务 4h 执行一次");
    }
}

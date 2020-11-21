package com.yuan.blog.timer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.management.OperatingSystemMXBean;
import com.yuan.blog.service.SystemLogService;
import com.yuan.blog.util.Cache;
import com.yuan.blog.vo.Poem.PoemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@EnableScheduling
@Component
public class ScheduledTask {

    @Resource
    private SystemLogService systemLogService;
    // 必须放在spring管理的类里才能正常注入JavaMailSender，否则就是null。怀疑是类实例化的时间先后有关
    @Autowired
    public JavaMailSender mailSender;
    @Autowired
    public RestTemplate restTemplate;
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.weibo.token}")
    private String token;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    //@Scheduled(fixedRate = 4 * 60 * 60 * 1000 ) // 24h 一次 项目第一次启动时就会执行 大部分时候启动时会执行两次。。还是用cron的好
    @Scheduled(cron = "0 53 22 * * ?")
    public void deleteSystemLog() {
        log.info("cron 定时任务 删除30天以前的日志(留下id小于、等于50的)");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -30);
        systemLogService.deleteLogsBefore(cal.getTime());
    }

    private PoemResponse getPoemForPosting(){
        Object obj = Cache.get(Cache.POEMS_PERFIX);
        if (obj == null) {
            refreshPoems();
            obj = Cache.get(Cache.POEMS_PERFIX);
        }
        PoemResponse poem = null;
        try {
            poem = ((List<PoemResponse>) obj).get(0);
        } catch (Exception e) {
            log.error("failed to cast catch poems " + obj + e.getMessage());
        }
        return poem;
    }

    // 每天获取古诗信息 从外部接口获取
    // json字符串转java实体 http://www.bejson.com/json2javapojo/new/
    @Scheduled(cron = "0 10 7 * * ?")
    public void refreshPoems() {
        log.info("cron 定时任务 start get poems!");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Token", "ZkGPsSMgyyXjw+muOnIqbaNmQ4iSgqfT");
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
        ObjectMapper mapper = new ObjectMapper();
        List<PoemResponse> poems = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            ResponseEntity<String> response = restTemplate.exchange("https://v2.jinrishici.com/sentence", HttpMethod.GET, requestEntity, String.class);
            PoemResponse resp = null;
            try {// 字符串转成实体类 反序列化
                resp = mapper.readValue(response.getBody(), PoemResponse.class);
            } catch (IOException e) {
                log.error("failed to readvalue for getPoems()", e);
            }
            if (resp != null) {
                poems.add(resp);
            }
        }
        if (!poems.isEmpty()) {// 设置一天有效期
            Cache.put(Cache.POEMS_PERFIX, poems, 1000 * 60 * 60 * 24);
        }

    }

    // 每10分钟移除一次过期的缓存
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void removeExpireCache() {
        int count = Cache.removeExpireCache();
        if (count > 0) {
            log.info("定时任务 移除 缓存数据数量：" + count);
        }
    }

    private void sendMail(String to, String text, String subject) {
        long start = System.currentTimeMillis();
        // 纯文本邮件。支持html or 附件需要MimeMessage
        // todo https://blog.csdn.net/larger5/article/details/80534887
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(to); // 收件人
            message.setSubject(subject);//主题
            message.setText(text); // 正文
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to sendmail " + e);
        }
        log.info("sendmail cost time " + (System.currentTimeMillis() - start) + " ms");
    }

    // 每分钟打印一次内存 信息到日志文件
    @Scheduled(fixedRate = 60 * 1000 * 5)
    public void printMemoryInfo() {
        int byteToMb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        long vmTotal = rt.totalMemory() / byteToMb;
        long vmFree = rt.freeMemory() / byteToMb;
        long vmMax = rt.maxMemory() / byteToMb;
        long vmUse = vmTotal - vmFree;
        log.info("================ start  ===============");
        log.info("JVM内存已用的空间为：" + vmUse + " MB");
        log.info("JVM内存的空闲空间为：" + vmFree + " MB");
        log.info("JVM总内存空间为(Xms)：" + vmTotal + " MB");
        log.info("JVM 可用最大内存空间(Xmx)：" + vmMax + " MB");
        // 操作系统级内存情况查询
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        String os = System.getProperty("os.name");
        long physicalFree = osmxb.getFreePhysicalMemorySize() / byteToMb;
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / byteToMb;
        long physicalUse = physicalTotal - physicalFree;
        log.info("操作系统的版本：" + os);
        log.info("操作系统物理内存已用的空间为：" + physicalFree + " MB");
        log.info("操作系统物理内存的空闲空间为：" + physicalUse + " MB");
        log.info("操作系统总物理内存：" + physicalTotal + " MB");

        // 获得线程总数
        ThreadGroup parentThread;
        int totalThread = 0;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                .getParent() != null; parentThread = parentThread.getParent()) {
            totalThread = parentThread.activeCount();
        }
        log.info("获得线程总数:" + totalThread);
        log.info("================ end  ===============");
    }
}

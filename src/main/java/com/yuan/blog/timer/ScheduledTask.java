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
    // �������spring����������������ע��JavaMailSender���������null����������ʵ������ʱ���Ⱥ��й�
    @Autowired
    public JavaMailSender mailSender;
    @Autowired
    public RestTemplate restTemplate;
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.weibo.token}")
    private String token;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    //@Scheduled(fixedRate = 4 * 60 * 60 * 1000 ) // 24h һ�� ��Ŀ��һ������ʱ�ͻ�ִ�� �󲿷�ʱ������ʱ��ִ�����Ρ���������cron�ĺ�
    @Scheduled(cron = "0 53 22 * * ?")
    public void deleteSystemLog() {
        log.info("cron ��ʱ���� ɾ��30����ǰ����־(����idС�ڡ�����50��)");
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

    // ÿ���ȡ��ʫ��Ϣ ���ⲿ�ӿڻ�ȡ
    // json�ַ���תjavaʵ�� http://www.bejson.com/json2javapojo/new/
    @Scheduled(cron = "0 10 7 * * ?")
    public void refreshPoems() {
        log.info("cron ��ʱ���� start get poems!");
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Token", "ZkGPsSMgyyXjw+muOnIqbaNmQ4iSgqfT");
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, headers);
        ObjectMapper mapper = new ObjectMapper();
        List<PoemResponse> poems = new ArrayList<>();
        for (int x = 0; x < 3; x++) {
            ResponseEntity<String> response = restTemplate.exchange("https://v2.jinrishici.com/sentence", HttpMethod.GET, requestEntity, String.class);
            PoemResponse resp = null;
            try {// �ַ���ת��ʵ���� �����л�
                resp = mapper.readValue(response.getBody(), PoemResponse.class);
            } catch (IOException e) {
                log.error("failed to readvalue for getPoems()", e);
            }
            if (resp != null) {
                poems.add(resp);
            }
        }
        if (!poems.isEmpty()) {// ����һ����Ч��
            Cache.put(Cache.POEMS_PERFIX, poems, 1000 * 60 * 60 * 24);
        }

    }

    // ÿ10�����Ƴ�һ�ι��ڵĻ���
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void removeExpireCache() {
        int count = Cache.removeExpireCache();
        if (count > 0) {
            log.info("��ʱ���� �Ƴ� ��������������" + count);
        }
    }

    private void sendMail(String to, String text, String subject) {
        long start = System.currentTimeMillis();
        // ���ı��ʼ���֧��html or ������ҪMimeMessage
        // todo https://blog.csdn.net/larger5/article/details/80534887
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(username);
            message.setTo(to); // �ռ���
            message.setSubject(subject);//����
            message.setText(text); // ����
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to sendmail " + e);
        }
        log.info("sendmail cost time " + (System.currentTimeMillis() - start) + " ms");
    }

    // ÿ���Ӵ�ӡһ���ڴ� ��Ϣ����־�ļ�
    @Scheduled(fixedRate = 60 * 1000 * 5)
    public void printMemoryInfo() {
        int byteToMb = 1024 * 1024;
        Runtime rt = Runtime.getRuntime();
        long vmTotal = rt.totalMemory() / byteToMb;
        long vmFree = rt.freeMemory() / byteToMb;
        long vmMax = rt.maxMemory() / byteToMb;
        long vmUse = vmTotal - vmFree;
        log.info("================ start  ===============");
        log.info("JVM�ڴ����õĿռ�Ϊ��" + vmUse + " MB");
        log.info("JVM�ڴ�Ŀ��пռ�Ϊ��" + vmFree + " MB");
        log.info("JVM���ڴ�ռ�Ϊ(Xms)��" + vmTotal + " MB");
        log.info("JVM ��������ڴ�ռ�(Xmx)��" + vmMax + " MB");
        // ����ϵͳ���ڴ������ѯ
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        String os = System.getProperty("os.name");
        long physicalFree = osmxb.getFreePhysicalMemorySize() / byteToMb;
        long physicalTotal = osmxb.getTotalPhysicalMemorySize() / byteToMb;
        long physicalUse = physicalTotal - physicalFree;
        log.info("����ϵͳ�İ汾��" + os);
        log.info("����ϵͳ�����ڴ����õĿռ�Ϊ��" + physicalFree + " MB");
        log.info("����ϵͳ�����ڴ�Ŀ��пռ�Ϊ��" + physicalUse + " MB");
        log.info("����ϵͳ�������ڴ棺" + physicalTotal + " MB");

        // ����߳�����
        ThreadGroup parentThread;
        int totalThread = 0;
        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                .getParent() != null; parentThread = parentThread.getParent()) {
            totalThread = parentThread.activeCount();
        }
        log.info("����߳�����:" + totalThread);
        log.info("================ end  ===============");
    }
}

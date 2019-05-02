package com.yuan.blog.timer;

import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.service.SystemLogService;
import com.yuan.blog.service.TodoService;
import com.yuan.blog.util.Cache;
import com.yuan.blog.util.NetUtil;
import com.yuan.blog.util.StringUtils;
import com.yuan.blog.util.TaskExecutor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.ws.soap.Addressing;
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
    @Autowired
    public RestTemplate restTemplate;
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.weibo.token}")
    private String token;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTask.class);

    //@Scheduled(fixedRate = 60 * 1000)
    @Scheduled(cron = "0 43 6 * * ?")
    public void weatherRReport() {
        log.info("cron 定时任务 发微博");
        String city = "深圳";
        StringBuilder sb = new StringBuilder(city + "今天");
        ResponseEntity<String> response = restTemplate.getForEntity("http://wthrcdn.etouch.cn/WeatherApi?city=" + city, String.class);
        if (response.getStatusCodeValue() == 200) {
            try {
                String strBody = response.getBody();
                JSONObject xmlJSONObj = XML.toJSONObject(strBody);
                JSONObject resp = xmlJSONObj.getJSONObject("resp");
                JSONObject forecast = resp.getJSONObject("forecast");
                JSONArray weather = forecast.getJSONArray("weather");
                JSONObject today = weather.getJSONObject(0);
                JSONObject day1 = weather.getJSONObject(1);
                JSONObject day2 = weather.getJSONObject(2);

                sb.append(StringUtils.getNumber(today.getString("low")));
                sb.append("℃-");
                sb.append(StringUtils.getNumber(today.getString("high"))).append("℃,");
                String dayType = today.getJSONObject("day").getString("type");
                String nightType = today.getJSONObject("night").getString("type");
                if (dayType.equals(nightType)) {
                    sb.append(dayType).append(",");
                } else {
                    sb.append("白天").append(dayType).append(",夜晚").append(nightType).append(",");
                }
                sb.append("明天");
                sb.append(StringUtils.getNumber(day1.getString("low")));
                sb.append("℃-");
                sb.append(StringUtils.getNumber(day1.getString("high"))).append("℃,");
                sb.append(day1.getJSONObject("day").getString("type")).append(",");

                sb.append("后天");
                sb.append(StringUtils.getNumber(day2.getString("low")));
                sb.append("℃-");
                sb.append(StringUtils.getNumber(day2.getString("high"))).append("℃,");
                sb.append(day2.getJSONObject("day").getString("type")).append("。");
            } catch (Exception e) {
                log.error("failed to parse response of weather!!!!", e);
                return;
            }
        } else {
            log.error("failed to get weather data  ");
            return;
        }

        String os = System.getProperty("os.name");
        if (os.equalsIgnoreCase("Linux")) {
            String command = "java -jar /root/tmp/weibo4j-oauth2.jar " + token + " " + sb.toString();
            TaskExecutor.exec(command);
        } else {
            String command = "java -jar D:\\code\\weibo4j-oauth2.jar " + token + " " + sb.toString();
            TaskExecutor.exec(command);
        }
    }

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
    @Scheduled(cron = "0 42 15 * * ?")
    public void todoNotify() {
        // 查询需要通知的 todo 这里要聚合一下 同一个用户，发一条邮件
        try {
            log.info("定时任务 发送提醒邮件 start：" + "count");
            List<TodoResponse> todoList = todoService.queryForNotify();
            if (todoList != null && todoList.size() > 0) {
                for (TodoResponse todo : todoList) {
                    sendMail(todo.getEmail(), todo.getContent() + new Date().toString(), "延期啦！！");
                }
            }
            Integer count = 10;
            log.info("定时任务 发送提醒邮件 end：" + "count");
        } catch (Exception e) {
            log.error("failed to todoNotify " + e);
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
}

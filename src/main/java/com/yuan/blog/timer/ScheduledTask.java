package com.yuan.blog.timer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.service.SystemLogService;
import com.yuan.blog.service.TodoService;
import com.yuan.blog.util.Cache;
import com.yuan.blog.util.StringUtils;
import com.yuan.blog.util.TaskExecutor;
import com.yuan.blog.vo.Poem.Data;
import com.yuan.blog.vo.Poem.Origin;
import com.yuan.blog.vo.Poem.PoemResponse;
import com.yuan.blog.vo.Todo;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
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
import java.util.*;

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

//    @Scheduled(cron = "0 40 8 * * ?")
//    @Scheduled(fixedRate = 1 * 60 * 1000 )
    public void weatherReport() throws InterruptedException {
        System.out.println(new Date());
        Thread.sleep((long)(Math.random() * 1000 * 60 * 30));
        System.out.println(new Date());
        if (Math.random() > 0.3) {
            log.info("cron 定时任务 发古诗 start");
            Data poem = getPoemForPosting().getData();
            Origin origin = poem.getOrigin();
            String poemStr = poem.getContent();
            poemStr += "--" + origin.getAuthor();
            poemStr += "(" + origin.getDynasty() + ")";
            poemStr += "《" + origin.getTitle() + "》";
            postWeibo(poemStr);
            log.info("cron 定时任务 poem post over" + poemStr);
            return;
        }

        log.info("cron 定时任务 发微博");
        String citys = "信阳,深圳";
        String[] cityArr = citys.split(",");
        StringBuilder sb = new StringBuilder();
        for (String city : cityArr) {
            sb.append("[").append(city).append("]").append("今天");
            try {
                ResponseEntity<String> response = restTemplate.getForEntity("http://wthrcdn.etouch.cn/WeatherApi?city=" + city, String.class);
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
                sb.append("℃~");
                sb.append(StringUtils.getNumber(day1.getString("high"))).append("℃,");
                sb.append(day1.getJSONObject("day").getString("type")).append(",");

                sb.append("后天");
                sb.append(StringUtils.getNumber(day2.getString("low")));
                sb.append("℃~");
                sb.append(StringUtils.getNumber(day2.getString("high"))).append("℃,");
                sb.append(day2.getJSONObject("day").getString("type")).append("。");
            } catch (Exception e) {
                e.printStackTrace();
                long failTime = System.currentTimeMillis();
                log.error("failed to get weather for " + city + " " + failTime, e);
                sb.append("没数据啊！！").append(e.getMessage()).append(failTime);
            }
        }
        postWeibo(sb.toString());
    }

    /**
     * 调发送微博的jar包
     */
    private void postWeibo(String content) {
        String os = System.getProperty("os.name");
        if (os.equalsIgnoreCase("Linux")) {
            String command = "java -jar /root/tmp/weibo4j-oauth2.jar " + token + " " + content;
            TaskExecutor.exec(command);
        } else {
            String command = "java -jar D:\\code\\weibo4j-oauth2.jar " + token + " " + content;
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
//    @Scheduled(cron = "0 10 7 * * ?")
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

    //        @Scheduled(fixedRate = 1 * 60 * 1000)
//    @Scheduled(cron = "0 13 19,8 * * ?")
    public void todoNotify() {
        // 查询需要通知的 待办事项
        try {
            int count = 0;
            log.info("scheduled task of sending notify email start：");
            Todo queryParam = new Todo();
            queryParam.setNotify(1);
            queryParam.setStatus(0); //未完成
            List<TodoResponse> todoList = todoService.queryForNotify(queryParam);
            if (todoList != null && todoList.size() > 0) {
                Map<String, List<TodoResponse>> userMap = new HashMap<>();
                for (TodoResponse todo : todoList) {
                    if (userMap.containsKey(todo.getEmail())) {
                        userMap.get(todo.getEmail()).add(todo);
                    } else {
                        List<TodoResponse> list = new ArrayList<>();
                        list.add(todo);
                        userMap.put(todo.getEmail(), list);
                    }
                }
                for (String email : userMap.keySet()) {
                    List<TodoResponse> todos = userMap.get(email);
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < todos.size(); x++) {
                        sb.append(x).append(",").append(todos.get(x).getContent()).append(".");
                    }
                    sendMail(email, sb.toString(), todos.size() + " 项待办的任务还没处理！");
                    count++;
                }
            }
            log.info("scheduled task of sending notify email end,count " + count);
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

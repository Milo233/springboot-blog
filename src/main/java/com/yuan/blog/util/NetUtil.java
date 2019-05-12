package com.yuan.blog.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuan.blog.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络相关 工具类
 */
public class NetUtil {

    private static List<String> ipList = new ArrayList<>();

    static {
        ipList.add("13.229.72.8"); // vpn 翻墙ip
        ipList.add("localhost"); //
        ipList.add("127.0.0.1");
        ipList.add("0:0:0:0:0:0:0:1");
        ipList.add("223.104.63.11");
    }

    private static String imageServerUrl = "https://upload.cc/image_upload";

    private static String perfix = "https://upload.cc/";

    /**
     * 因为移动设备的ip是变化的，所以固定ip时只能用路由器上网
     */
    public static boolean isAllowed(HttpServletRequest request) {
        if (request == null) return false;
        String ipAddr = getIpAddr(request);
        return ipList.contains(ipAddr);
    }

    // 获取并记录ip
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    // 查询当前登录的用户
    public static User getCurrentUser(boolean checkLogin) {

        User user = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()
                .equals("anonymousUser")) {
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        // 需要校验登录 且当前未登录时抛出异常
        if (checkLogin && user == null) {
            throw new RuntimeException();
        } else {
            return user;
        }
    }

    /**
     * 图片上传
     * https://upload.cc/image_upload
     */
    public static String uploadImage(HttpServletRequest request, MultipartFile file) {
        String avatarUrl = "";
        try {
            MultipartUtility multipart = new MultipartUtility(imageServerUrl, "UTF-8");
            multipart.addHeaderField("User-Agent", "CodeJava");
            multipart.addHeaderField("Test-Header", "Header-Value");
            multipart.addFormField("description", "Cool Pictures");
            multipart.addFormField("keywords", "Java,upload,Spring");
            multipart.addFilePart("uploaded_file[]", multipart.analyzeFile(file, request));
            List<String> response = multipart.finish();
            StringBuilder sb = new StringBuilder();

            for (String line : response) {
                sb.append(line);
            }
            JSONObject jsonObject = JSONObject.parseObject(sb.toString());
            Object total_success = jsonObject.get("total_success");
            if ("1".equals(total_success.toString())) {
                Object success_image = jsonObject.get("success_image");
                JSONArray objects = JSONObject.parseArray(success_image.toString());
                JSONObject jsonObject1 = JSONObject.parseObject(objects.get(0).toString());
                avatarUrl = perfix + jsonObject1.get("url"); // 图片路径
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return avatarUrl;
    }
}

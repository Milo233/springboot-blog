package com.yuan.blog.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuan.blog.domain.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

/**
 * 网络相关 工具类
 */
public class NetUtil {

    private static String imageServerUrl = "https://upload.cc/image_upload";

    private static String perfix = "https://upload.cc/";

    // 获取ip
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) return "";
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
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
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

    /**
     * 从指定url下载资源
     *
     * @param imgUrl   文件路径
     * @param savePath 保存路径
     */
    public static String uploadFromUrl(String imgUrl, HttpServletRequest request, String savePath) throws IOException {
        URL url = new URL(imgUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(genenrateImageFileName(imgUrl)); // saveDir + File.separator +
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);

        FileItem fileItem = new DiskFileItem("mainFile", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());

        try {
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);
            // Or faster..
            // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
        } catch (IOException ex) {
            // do something.
        }

        MultipartFile multipartFile  = new CommonsMultipartFile(fileItem);;

        String newUrl = uploadImage(request, multipartFile);
        fos.close();
        inputStream.close();
        return newUrl;
    }

    // 分析文件类型 拼接文件名
    private static String genenrateImageFileName(String imgUrl) {
        if (imgUrl == null || imgUrl.isEmpty()) {
            throw new IllegalArgumentException("empty imgUrl!!");
        }
        imgUrl = imgUrl.toLowerCase();
        if (imgUrl.contains("png")) {
            return UUID.randomUUID() + ".png";
        } else if (imgUrl.contains("gif")) {
            return UUID.randomUUID() + ".gif";
        } else {
            return UUID.randomUUID() + ".jpg";
        }
    }

    /**
     * 从输入流中获取字节数组
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}

package com.yuan.blog.util;

import com.alibaba.fastjson.JSONObject;
import com.yuan.blog.domain.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 网络相关 工具类
 */
@Component
public class NetUtil {
    private static String diskUserName;
    private static String diskPassWord;

    @Value("${diskUserName}")
    public void setEnv(String diskUserName) {
        NetUtil.diskUserName = diskUserName;
    }
    @Value("${diskPassWord}")
    public void setDiskPassWord(String diskPassWord) {
        NetUtil.diskPassWord = diskPassWord;
    }

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
     */
    public static String uploadImage(HttpServletRequest request, MultipartFile file,String name) throws IOException {
        String newName = genenrateImageFileName(name);
        String uploadToken = getUploadToken(newName);
        String result = httpClientUploadFile("http://47.90.97.197:6010/api/alien/upload",
                multipartFileToFile(file, request,newName), uploadToken);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject != null && "OK".equals(jsonObject.getString("code"))) {
            JSONObject date = jsonObject.getJSONObject("data");
            return "http://47.90.97.197:6010/api/alien/preview/" + date.getString("uuid") + "/" + date.getString("name");
        }

        return "";
    }

    public static File multipartFileToFile(MultipartFile file, HttpServletRequest request,String newName) {
        File tempFile = null;
        if (!file.isEmpty()) {
            String filePath = request.getSession().getServletContext().getRealPath("/") + "upload/";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }

            String path = filePath + newName;
            try {
                tempFile = new File(path);
                FileUtils.copyInputStreamToFile(file.getInputStream(), tempFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }

    public static File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
        File convFile = new File(multipart.getOriginalFilename());
        multipart.transferTo(convFile);
        return convFile;
    }

    public static String httpClientUploadFile(String url, File file, String token) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String result = "";
        //每个post参数之间的分隔。随意设定，只要不会和其他的字符串重复即可。
        String boundary = "--------------4585696313564699";
        try {
            HttpPost httpPost = new HttpPost(url);
            //设置请求头
            httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

            //HttpEntity builder
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            //字符编码
            builder.setCharset(Charset.forName("UTF-8"));
            //模拟浏览器
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            //boundary
            builder.setBoundary(boundary);
            //multipart/form-data
            builder.addPart("file", new FileBody(file));
            // binary
//            builder.addBinaryBody("name=\"multipartFile\"; filename=\"test.docx\"", new FileInputStream(file), ContentType.MULTIPART_FORM_DATA, fileName);// 文件流
            //其他参数
            builder.addTextBody("uploadTokenUuid", token, ContentType.create("text/plain", Consts.UTF_8));
            //HttpEntity
            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);
            // 执行提交
            HttpResponse response = httpClient.execute(httpPost);
            //响应
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                // 将响应内容转换为字符串
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static Map<String, String> cookieMap = new HashMap<>();

    private static String getUploadToken(String newName) throws IOException {
        // todo 网盘可以改成login 请求上传token接口合并 减少请求次数
        if (!getCookies(diskUserName, diskPassWord)) {
            throw new RuntimeException("failed to login to get cookie");
        }
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("filename", newName);
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, 2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        paraMap.put("expireTime", sdf.format(instance.getTime()));//  dirPath
        paraMap.put("size", "5");
        paraMap.put("dirPath", "/image/"); // todo 不用用户项目。目录区分
        CookieStore cookieStore = new BasicCookieStore();
        for (String key : cookieMap.keySet()) {
            BasicClientCookie cookie = new BasicClientCookie(key, cookieMap.get(key));
            cookie.setVersion(0);
            cookie.setDomain("47.90.97.197");
            cookie.setPath("/");
            cookie.setAttribute(BasicClientCookie.PATH_ATTR, "/");
            cookie.setAttribute(BasicClientCookie.DOMAIN_ATTR, "47.90.97.197");
            cookieStore.addCookie(cookie);
        }
        HttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost post = new HttpPost("http://47.90.97.197:6010/api/alien/fetch/upload/token");
        List paramsList = new ArrayList();
        Set<String> keySet = paraMap.keySet();
        for (String key : keySet) {
            paramsList.add(new BasicNameValuePair(key, paraMap.get(key)));
        }
        UrlEncodedFormEntity entitys = new UrlEncodedFormEntity(paramsList, Consts.UTF_8);
        post.setEntity(entitys);
        HttpResponse response = null;
        try {
            response = httpclient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8.displayName());
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && "OK".equals(jsonObject.get("code"))) {
                JSONObject data = jsonObject.getJSONObject("data");
                return data.getString("uuid");
            }
        }
        return null;
    }

    private static boolean getCookies(String username, String password) {
        cookieMap.clear();
        CloseableHttpClient client = HttpClients.createDefault();
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        String authUrl = getUrl("http://47.90.97.197:6010/api/user/login", map);
//        System.out.println("authUrl = " + authUrl);
        HttpPost post = new HttpPost(authUrl);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
//            client.close(); 用了连接池可能不用手动释放
        }
        if (response != null) {
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                Header cookieHeader = response.getFirstHeader("Set-Cookie");
                String cookies = cookieHeader.getValue();
                String[] split = cookies.split(";");
                String s = split[0];
                String[] split1 = s.split("=");
                cookieMap.put(split1[0], split1[1]);
                return true;
            }
        }
        return false;
    }

    private static String getUrl(String url, Map<String, String> paramMap) {
        if (url == null || url.isEmpty()) {
            System.out.println("url is empty");
            return "-1";
        }
        if (paramMap != null && !paramMap.isEmpty()) {
            url = url + mapToUrl(paramMap);
        }
        return url;
    }


    public static String mapToUrl(Map<String, String> map) {
        String arguments = "?";
        Iterator iterator = map.entrySet().iterator();
        Boolean first_args = true;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            String key_str = (String) key;
            String val_str = (String) val;
            if (first_args == false) {
                arguments = arguments + "&";
            }
            first_args = false;
            arguments = arguments + key_str;
            arguments = arguments + "=";
            arguments = arguments + val_str;

        }
        return arguments;
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

        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        String newUrl = uploadImage(request, multipartFile,imgUrl);
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

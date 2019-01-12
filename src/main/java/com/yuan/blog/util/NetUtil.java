package com.yuan.blog.util;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络相关 工具类
 */
public class NetUtil {

    private static List<String> ipList = new ArrayList<>();

    static {
        ipList.add("112.97.212.97"); // 在家 手机ip
        ipList.add("13.229.72.8"); // vpn 翻墙ip
        ipList.add("localhost"); //
        ipList.add("127.0.0.1");
        ipList.add("0:0:0:0:0:0:0:1");
        ipList.add("223.104.63.11");
    }

    public static boolean isAllowed(HttpServletRequest request){
        if(request == null) return false;

        String ipAddr = getIpAddr(request);
        return ipList.contains(ipAddr);
    }

    // 获取ip
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
}

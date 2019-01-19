package com.yuan.blog.util;

import com.yuan.blog.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    /**
     * 因为移动设备的ip是变化的，所以固定ip时只能用路由器上网
     */
    public static boolean isAllowed(HttpServletRequest request){
        if(request == null) return false;
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
    public static User getCurrentUser(){
        if (SecurityContextHolder.getContext().getAuthentication() !=null
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()
                .equals("anonymousUser")) {
            return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        return null;
    }
}

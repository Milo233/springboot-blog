package com.yuan.blog.service;

import com.yuan.blog.dao.BlogDao;
import com.yuan.blog.domain.User;
import com.yuan.blog.util.NetUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;

@Service
public class SystemLogServiceImpl implements SystemLogService{

    @Resource
    private BlogDao blogDao;

    /**
     * 插入操作日志
     * ip解析，要求精确的话可能要异步调外部api，
     * 不很要求精度的可以把本地ip-地址映射的数据加载到内存里在内存里匹配，guava有个rangeMap很快
     */
    @Override
    public int insertSystemLog(HttpServletRequest request, User user,String title) {
        String ip = NetUtil.getIpAddr(request);
        // 排除开发模式
        if ("127.0.0.1".equals(ip)) return 0;
        // http://ip.taobao.com/service/getIpInfo.php?ip=61.144.248.17
//        根据ip查询最近的登录记录。用于判断ip归属地/
        HashMap<String, String> map = new HashMap<>();
        map.put("title",title);
        map.put("account",user == null ? "" : user.getUsername());
        map.put("loginIP", ip);
        map.put("loginArea",null);
        return blogDao.insertSystemLog(map);
    }

    @Override
    public int deleteLogsBefore(Date date){
        return blogDao.deleteLogsBefore(date);
    }
}



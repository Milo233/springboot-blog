package com.yuan.blog.service;

import com.yuan.blog.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * SystemLog 服务接口.
 */
public interface SystemLogService {

	int insertSystemLog(HttpServletRequest request, User user,String title);

	// 删除指定日期之前的日志
	int deleteLogsBefore(Date date);
}

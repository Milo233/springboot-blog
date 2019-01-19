package com.yuan.blog.service;

import com.yuan.blog.domain.User;

import javax.servlet.http.HttpServletRequest;

/**
 * SystemLog 服务接口.
 */
public interface SystemLogService {

	int insertSystemLog(HttpServletRequest request, User user,String title);
}

package com.yuan.blog.service;

import com.yuan.blog.response.TodoResponse;

import java.util.List;

/**
 * Blog 服务接口.
 */
public interface TodoService {

    List<TodoResponse> queryForNotify();
}

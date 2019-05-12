package com.yuan.blog.service;

import com.yuan.blog.domain.User;
import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.vo.Todo;

import java.util.List;

/**
 * Blog 服务接口.
 */
public interface TodoService {

    List<TodoResponse> queryForNotify(Todo todo);

    int addTodo(Todo todo);

    int updateTodo(Todo todo, User user);
}

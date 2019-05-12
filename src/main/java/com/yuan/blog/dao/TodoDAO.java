package com.yuan.blog.dao;

import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.vo.Todo;

import java.util.List;

public interface TodoDAO {
    int deleteByPrimaryKey(Long id);

    int insert(Todo record);

    int insertSelective(Todo record);

    Todo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Todo record);

    int updateByPrimaryKey(Todo record);
    // 查询todo list
    List<TodoResponse> queryForNotify(Todo record);
}
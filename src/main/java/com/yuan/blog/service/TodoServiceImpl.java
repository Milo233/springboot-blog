package com.yuan.blog.service;

import com.yuan.blog.dao.TodoDAO;
import com.yuan.blog.response.TodoResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Resource
    private TodoDAO todoDao;

    @Override
    public List<TodoResponse> queryForNotify() {
        return todoDao.queryForNotify();
    }
}

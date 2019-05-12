package com.yuan.blog.service;

import com.yuan.blog.dao.TodoDAO;
import com.yuan.blog.domain.User;
import com.yuan.blog.response.TodoResponse;
import com.yuan.blog.vo.Todo;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Resource
    private TodoDAO todoDao;

    @Override
    public List<TodoResponse> queryForNotify(Todo todo) {
        return todoDao.queryForNotify(todo);
    }

    @Override
    public int addTodo(Todo todo) {
        todo.setCreateTime(new Date());
        todo.setStatus(0);
        todo.setDeleted(0);
        return todoDao.insert(todo);
    }

    @Override
    public int updateTodo(Todo todo, User user) {
        // 判断是否是创建者
        Todo record = todoDao.selectByPrimaryKey(todo.getId());
        if (record == null || record.getDeleted() != 0 || !user.getId().equals(record.getUserId())) {
            throw new InvalidParameterException("记录不存在 or 权限不够");
        }
        return todoDao.updateByPrimaryKeySelective(todo);
    }
}

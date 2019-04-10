package com.yuan.blog.dao;

import com.yuan.blog.vo.Todo;

public interface TodoDAO {
    int deleteByPrimaryKey(Long id);

    int insert(Todo record);

    int insertSelective(Todo record);

    Todo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Todo record);

    int updateByPrimaryKey(Todo record);
}
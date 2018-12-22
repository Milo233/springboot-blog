package com.yuan.blog.dao;

import com.yuan.blog.domain.Blog;

public interface BlogDao {
    Blog selectByPrimaryKey(Integer id);
}

package com.yuan.blog.dao;

import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Tally;

import java.util.HashMap;
import java.util.List;

public interface BlogDao {
    Blog selectByPrimaryKey(Integer id);

    List<Blog> queryList(HashMap map);

    int insertSystemLog(HashMap map);

    int saveTally(Tally tally);
}

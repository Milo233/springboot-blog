package com.yuan.blog.dao;

import com.yuan.blog.domain.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BlogDao {
    Blog selectByPrimaryKey(Integer id);

    List<Blog> queryList(HashMap map);

    int insertSystemLog(HashMap map);

    int deleteLogsBefore(Date date);

    int saveTally(Tally tally);

    List<TalleyCollection> collectTalley(String userName);

    int createComment(CommentV2 comment);

    int insertBlogComment(Map<String,Object> map);

    int deleteComment(Long id);

    int deleteBlogComment(Long id);

    Blog selectByCatalog(Long catalog);
}

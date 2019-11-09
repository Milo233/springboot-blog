package com.yuan.blog.service;

import com.yuan.blog.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Blog 服务接口.
 */
public interface BlogService {
    /**
     * 保存Blog
     */
    Blog saveBlog(Blog blog);

    /**
     * 删除Blog
     */
    void removeBlog(Long id);

    /**
     * 更新Blog
     */
    Blog updateBlog(Blog blog);

    /**
     * 根据id获取Blog
     */
    Blog getBlogById(Long id);

    Page<Blog> listBlogsByUserAndKeywordByHot(User user, String keyword, Pageable pageable);

    Page<Blog> ll(String keyword);

    Page<Blog> listBlogsByKeywordByHot(String keyword, Pageable pageable);

    /**
     * 根据用户名进行分页模糊查询（最新）
     */
    Page<Blog> listBlogsByUserAndKeywordByTime(User user, String title, Pageable pageable);

    // （最新）
    Page<Blog> listBlogsByKeywordByTime(String title, Pageable pageable);

    /**
     * 阅读量递增
     */
    void readingIncrease(Long id);

    /**
     * 发表评论
     */
    int createComment(Long blogId, String commentContent, Long userId);

    /**
     * 删除评论
     */
    void removeComment(Long commentId);

    /**
     * 点赞
     */
    Blog createVote(Long blogId);

    /**
     * 取消点赞
     */
    void removeVote(Long blogId, Long voteId);

    /**
     * 根据分类进行查询
     */
    Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable);

    void saveTally(List<Tally> tallyList, User user);

    int updateSystemLog(String content, int id);

    String getContentById(int id);

    List<TalleyCollection> collectTalley(String userName);
}

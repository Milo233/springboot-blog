package com.yuan.blog.service;

import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Blog 服务接口.
 * 
 * @since 1.0.0 2017年4月7日
 * @author <a href="https://waylau.com">Way Lau</a>
 */
public interface BlogService {
	/**
	 * 保存Blog
	 * @param blog
	 * @return
	 */
	Blog saveBlog(Blog blog);
	
	/**
	 * 删除Blog
	 * @param id
	 * @return
	 */
	void removeBlog(Long id);
	
	/**
	 * 更新Blog
	 * @param blog
	 * @return
	 */
	Blog updateBlog(Blog blog);
	
	/**
	 * 根据id获取Blog
	 * @param id
	 * @return
	 */
	Blog getBlogById(Long id);

	Page<Blog> listBlogsByUserAndKeywordByHot(User user, String keyword, Pageable pageable);

	Page<Blog> listBlogsByKeywordByHot(String keyword, Pageable pageable);
	/**
	 * 根据用户名进行分页模糊查询（最新）
	 * @param user
	 * @return
	 */
	Page<Blog> listBlogsByUserAndKeywordByTime(User user, String title, Pageable pageable);

	// （最新）
	Page<Blog> listBlogsByKeywordByTime(String title, Pageable pageable);

	/**
	 * 阅读量递增
	 * @param id
	 */
	void readingIncrease(Long id);

	/**
	 * 发表评论
	 * @param blogId
	 * @param commentContent
	 * @return
	 */
	Blog createComment(Long blogId, String commentContent);

	/**
	 * 删除评论
	 * @param blogId
	 * @param commentId
	 * @return
	 */
	void removeComment(Long blogId, Long commentId);

	/**
	 * 点赞
	 * @param blogId
	 * @return
	 */
	Blog createVote(Long blogId);

	/**
	 * 取消点赞
	 * @param blogId
	 * @param voteId
	 * @return
	 */
	void removeVote(Long blogId, Long voteId);

	/**
	 * 根据分类进行查询
	 * @param catalog
	 * @param pageable
	 * @return
	 */
	Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable);
}

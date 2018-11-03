package com.yuan.blog.repository;

import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Blog 仓库.
 */
public interface BlogRepository extends JpaRepository<Blog, Long>{

	/**
	 * 根据用户名分页查询博客列表
	 * @param user
	 * @param title
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndTitleLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);

	/**
	 * 根据用户 && (博客标题 || 博客标签) 分页查询博客列表
	 */
	Page<Blog> findByUserAndTitleLikeOrUserAndTagsLike(User user, String title, User user2, String tag, Pageable pageable);

	/**
	 * (博客标题 || 博客标签) 分页查询博客列表 不含用户
	 */
	Page<Blog> findByTitleLikeOrTagsLike(String title,String tag, Pageable pageable);

	/**
	 * 根据用户名分页查询博客列表
	 * @param user
	 * @param title
	 * @param pageable
	 * @return
	 */
	Page<Blog> findByUserAndTitleLike(User user, String title, Pageable pageable);


	/**
	 * 根据分类查询博客列表
	 * @param catalog
	 * @param pageable
	 */
	Page<Blog> findByCatalog(Catalog catalog, Pageable pageable);
}

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

	Page<Blog> findByUserAndTitleOrTagsLikeOrderByCreateTimeDesc(User user, String title, Pageable pageable);

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

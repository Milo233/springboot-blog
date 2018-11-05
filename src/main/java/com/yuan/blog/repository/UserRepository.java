package com.yuan.blog.repository;

import com.yuan.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * 用户仓库.
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * 根据用户名分页查询用户列表
	 */
	Page<User> findByNameLike(String name, Pageable pageable);
	
	User findByUsername(String username);

	/** 根据邮箱查询会员*/
	User findByEmail(String email);

	/**
	 * 根据名称列表查询用户列表
	 * @param usernames
	 * @return
	 */
	List<User> findByUsernameIn(Collection<String> usernames);
}

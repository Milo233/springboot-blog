package com.yuan.blog.service;

import com.yuan.blog.domain.Authority;

import java.util.Optional;

/**
 * Authority 服务接口.
 */
public interface AuthorityService {

	/**
	 * 根据ID查询 Authority
	 */
	Optional<Authority> getAuthorityById(Long id);

	/**
	 * 根据角色名称 查询 Authority
	 */
	Optional<Authority> getAuthorityByName(String name);

	/**
	 * 新增权限
	 */
	Authority saveAuthority(Authority authority);
	/**
	 * 查询Authority数量
	 */
	long count();
}

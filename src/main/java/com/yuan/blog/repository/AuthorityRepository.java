package com.yuan.blog.repository;

import com.yuan.blog.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Authority 仓库.
 */
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    /**
     * 根据角色名称 获取 Authority
     */
    Optional<Authority> findByName(String name);
	
}

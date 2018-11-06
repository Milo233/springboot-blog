package com.yuan.blog.service;

import com.yuan.blog.domain.Authority;
import com.yuan.blog.repository.AuthorityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Authority 服务接口的实现.
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Override
	public Optional<Authority> getAuthorityById(Long id) {
		return authorityRepository.findById(id);
	}

	@Override
	public Optional<Authority> getAuthorityByName(String name) {
		return authorityRepository.findByName(name);
	}

	@Override
	public Authority saveAuthority(Authority authority) {
		return authorityRepository.save(authority);
	}

	@Override
	public long count() {
		return authorityRepository.count();
	}
}

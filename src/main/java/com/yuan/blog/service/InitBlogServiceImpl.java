package com.yuan.blog.service;

import com.yuan.blog.domain.Authority;
import com.yuan.blog.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 初始化博客 服务接口.
 */
@Service
public class InitBlogServiceImpl implements InitBlogService {

	@Autowired
	private AuthorityService authorityService;
	@Autowired
	private UserService userService;

	@Override
	public void initAdmin() {
		//如果不存在authority，则添加admin和user authority
		long count = authorityService.count();
		if(count == 0){
			authorityService.saveAuthority(new Authority(Authority.NAME_ADMIN));
			authorityService.saveAuthority(new Authority(Authority.NAME_USER));
		}
		//如果不存在 管理员，则添加一个管理员
		Optional<Authority> adminAuthority = authorityService.getAuthorityByName(Authority.NAME_ADMIN);
		List<User> userList = userService.getUserByAuthority(adminAuthority.get());

		if(userList.isEmpty()){
			PasswordEncoder encoder = new BCryptPasswordEncoder();
			User admin1 = new User("milo", "milo", "milo@gmail.com",encoder.encode("111111"));
			User admin2 = new User("venna", "venna", "venna@gmail.com",encoder.encode("111111"));
			List<Authority> authorities = new ArrayList<>();
			authorities.add(adminAuthority.get());
			admin1.setAuthorities(authorities);
			admin2.setAuthorities(authorities);

			userService.saveOrUpdateUser(admin1);
			userService.saveOrUpdateUser(admin2);
		}
	}
}

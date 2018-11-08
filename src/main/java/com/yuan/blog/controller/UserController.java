package com.yuan.blog.controller;

import com.yuan.blog.domain.Authority;
import com.yuan.blog.domain.User;
import com.yuan.blog.service.AuthorityService;
import com.yuan.blog.service.UserService;
import com.yuan.blog.util.ConstraintViolationExceptionHandler;
import com.yuan.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器.
 */
@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired 
	private UserService userService;
	@Autowired
	private AuthorityService authorityService;

	/**
	 * 从 用户存储库 获取用户列表
	 * @return
	 */
	private List<User> getUserlist() {
 		return userService.listUsers();
	}

	/**
	 * 查询所用用户
	 * @return
	 */
	@GetMapping
	public ModelAndView list(@RequestParam(value="async",required=false) boolean async,
							 @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
							 @RequestParam(value="pageSize",required=false,defaultValue="5") int pageSize,
							 @RequestParam(value="name",required=false,defaultValue="") String name,
							 Model model) {

		Pageable pageable = PageRequest.of(pageIndex, pageSize);
		Page<User> page = userService.listUsersByNameLike(name, pageable);
		List<User> list = page.getContent();	// 当前所在页面数据列表

		model.addAttribute("page", page);// 分页数据
		model.addAttribute("userList", list);
		return new ModelAndView(async?"users/list :: #mainContainerRepleace":"users/list", "userModel", model);
	}

	/**
	 * 根据id查询用户
	 * @param id model
	 * @return
	 */
	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") Long id, Model model) {
		// 早期版本是 getOne()
		User user = userService.getUserById(id);
		model.addAttribute("user", user);
		model.addAttribute("title", "查看用户");
		return new ModelAndView("users/view", "userModel", model);
	}

	/**
	 * 获取 form 表单页面
	 */
	@GetMapping("/add")
	public ModelAndView createForm(Model model) {
		model.addAttribute("user", new User(null,null,null,null));
		model.addAttribute("title", "创建用户");
		return new ModelAndView("users/add", "userModel", model);
	}

	/**
	 * 删除用户
	 */
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response>  delete(@PathVariable("id") Long id, Model model) {
		userService.removeUser(id);

		model.addAttribute("userList", userService.listUsers());
		model.addAttribute("title", "删除用户");
		// 删除完 跳转list页面
//		return new ModelAndView("users/list", "userModel", model);
		return ResponseEntity.ok().body(new Response(true, "处理成功"));
	}

	/**
	 * 新建用户
	 * @param user
	 */
	@PostMapping
	public ResponseEntity<Response> create(User user, Long authorityId) {
		List<Authority> authorities = new ArrayList<>();
		// todo 下面会报错 选择角色的前端写死了
		authorities.add(authorityService.getAuthorityById(authorityId).get());
		user.setAuthorities(authorities);

		if(user.getId() == null) {
//			user.setEncodePassword(user.getPassword()); // 加密密码
		}else {
			// 判断密码是否做了变更
			User originalUser = userService.getUserById(user.getId());
			String rawPassword = originalUser.getPassword();
//			PasswordEncoder encoder = new BCryptPasswordEncoder();
//			String encodePasswd = encoder.encode(user.getPassword());
//			boolean isMatch = encoder.matches(rawPassword, encodePasswd);
//			if (!isMatch) {
//				user.setEncodePassword(user.getPassword());
//			}else {
//				user.setPassword(user.getPassword());
//			}
		}
		try {
			userService.saveOrUpdateUser(user);
		}  catch (ConstraintViolationException e)  {
			// org.hibernate.exception.ConstraintViolationException: could not execute statement
			// 捕获失败 idk why 哭唧唧
			return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
		} catch (Exception e) {
			return ResponseEntity.ok().body(new Response(false, e.getMessage()));
		}
		return ResponseEntity.ok().body(new Response(true, "处理成功", user));
	}

	/**
	 * 修改用户
	 */
	@GetMapping(value = "edit/{id}")
	public ModelAndView modifyForm(@PathVariable("id") Long id, Model model) {
		User user = userService.getUserById(id);
 
		model.addAttribute("user", user);
		model.addAttribute("title", "修改用户");
		return new ModelAndView("users/edit", "userModel", model);
	}

}

package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 用户控制器.
 */
@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired 
	private UserService userService;

	/**
	 * 从 用户存储库 获取用户列表
	 * @return
	 */
	private List<User> getUserlist() {
// 		return userService.listUser();
		return null;
	}

	/**
	 * 查询所用用户
	 * @return
	 */
	@GetMapping
	public ModelAndView list(Model model) {
		model.addAttribute("userList", userService.findAll());
		model.addAttribute("title", "用户管理");
		return new ModelAndView("users/list", "userModel", model);
	}

	/**
	 * 查询所用用户
	 *  todo 要改么。。
	 */
	@GetMapping("/version2")
	public ModelAndView list2() {
		return new ModelAndView("users/list");
	}
 
	/**
	 * 根据id查询用户
	 * @param id model
	 * @return
	 */
	@GetMapping("{id}")
	public ModelAndView view(@PathVariable("id") Long id, Model model) {
		// 早期版本是 getOne()
		User user = userService.findById(id).get();
		model.addAttribute("user", user);
		model.addAttribute("title", "查看用户");
		return new ModelAndView("users/view", "userModel", model);
	}

	/**
	 * 获取 form 表单页面
	 * @param model
	 * @return
	 */
	@GetMapping("/form")
	public ModelAndView createForm(Model model) {
		model.addAttribute("user", new User(null,null));
		model.addAttribute("title", "创建用户");
		return new ModelAndView("users/form", "userModel", model);
	}

	/**
	 * 新建用户
	 */
	@PostMapping
	public ModelAndView create(User user) {
 		userService.save(user);
		return new ModelAndView("redirect:/users");
	}

	/**
	 * 删除用户
	 */
	@GetMapping(value = "delete/{id}")
	public ModelAndView delete(@PathVariable("id") Long id, Model model) {
		userService.deleteById(id);
 
		model.addAttribute("userList", userService.findAll());
		model.addAttribute("title", "删除用户");
		// 删除完 跳转list页面
		return new ModelAndView("users/list", "userModel", model);
	}

	/**
	 * 修改用户
	 */
	@GetMapping(value = "modify/{id}")
	public ModelAndView modifyForm(@PathVariable("id") Long id, Model model) {
		User user = userService.findById(id).get();
 
		model.addAttribute("user", user);
		model.addAttribute("title", "修改用户");
		return new ModelAndView("users/form", "userModel", model);
	}

}

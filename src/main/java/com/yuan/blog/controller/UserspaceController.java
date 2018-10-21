package com.yuan.blog.controller;

import com.yuan.blog.domain.User;
import com.yuan.blog.service.UserService;
import com.yuan.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 用户主页空间控制器.
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserDetailsService userDetailsService;
	@Value("$(file.server.url)")
	private String fileServerUrl;

	/**
	 *  获取用户配置
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView profile(@PathVariable("username") String username, Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		model.addAttribute("fileServerUrl", fileServerUrl);
		return new ModelAndView("/userspace/profile", "userModel", model);
	}

	/**
	 * 保存个人设置
	 */
	@PostMapping("/{username}/profile")
	@PreAuthorize("authentication.name.equals(#username)")
	public String saveProfile(@PathVariable("username") String username,User user) {
		User originalUser = userService.getUserById(user.getId());
		originalUser.setEmail(user.getEmail());
		originalUser.setName(user.getName());

		// 判断密码是否做了变更
		String rawPassword = originalUser.getPassword();
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePasswd = encoder.encode(user.getPassword());
		boolean isMatch = encoder.matches(rawPassword, encodePasswd);
		if (!isMatch) {
			originalUser.setEncodePassword(user.getPassword());
		}

		userService.saveOrUpdateUser(originalUser);
		return "redirect:/u/" + username + "/profile";
	}

	/**
	 * 获取编辑头像的界面
	 */
	@GetMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)")
	public ModelAndView avatar(@PathVariable("username") String username, Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return new ModelAndView("/userspace/avatar", "userModel", model);
	}

	/**
	 * 保存头像
	 */
	@PostMapping("/{username}/avatar")
	@PreAuthorize("authentication.name.equals(#username)")
	public ResponseEntity<Response> saveAvatar(@PathVariable("username") String username, @RequestBody User user) {
		String avatarUrl = user.getAvatar();

		User originalUser = userService.getUserById(user.getId());
		// localhost:8081/view/5bcc58649e76f8d8e4f9cd90
		// 存的是mongodb 的id
		originalUser.setAvatar(avatarUrl);
		userService.saveOrUpdateUser(originalUser);
		return ResponseEntity.ok().body(new Response(true, "处理成功", avatarUrl));
	}

	// 根据用户名 获取用户信息
	@GetMapping("/{username}")
	public String userSpace(@PathVariable("username") String username, Model model) {
		//todo 哪里掉这里了？？？
		if(StringUtils.isEmpty(username)){
			username = "milo";
		}
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		return "redirect:/u/" + username + "/blogs";
	}
 
	@GetMapping("/{username}/blogs")
	public String listBlogsByOrder(@PathVariable("username") String username,
								   @RequestParam(value="order",required=false,defaultValue="new") String order,
								   @RequestParam(value="category",required=false ) Long category,
								   @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
								   @RequestParam(value="async",required=false) boolean async,
								   @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
								   @RequestParam(value="pageSize",required=false,defaultValue="10") int pageSize,
								   Model model) {
		User  user = (User)userDetailsService.loadUserByUsername(username);
		model.addAttribute("user", user);
		if (category != null) {
			
			System.out.print("category:" +category );
			System.out.print("selflink:" + "redirect:/u/"+ username +"/blogs?category="+category);
			return "/userspace/u";
			
		} else if (keyword != null && keyword.isEmpty() == false) {
			
			System.out.print("keyword:" +keyword );
			System.out.print("selflink:" + "redirect:/u/"+ username +"/blogs?keyword="+keyword);
			return "/userspace/u";
		}  
		
		System.out.print("order:" +order);
		System.out.print("selflink:" + "redirect:/u/"+ username +"/blogs?order="+order);
		return "/userspace/u";
	}
	
	@GetMapping("/{username}/blogs/{id}")
	public String listBlogsByOrder(@PathVariable("id") Long id) {
		 
		System.out.print("blogId:" + id);
		return "/userspace/blog";
	}
	
	
	@GetMapping("/{username}/blogs/edit")
	public String editBlog() {
 
		return "/userspace/blogedit";
	}
}

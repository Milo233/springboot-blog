package com.yuan.blog.controller;

import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;
import com.yuan.blog.service.CatalogService;
import com.yuan.blog.util.ConstraintViolationExceptionHandler;
import com.yuan.blog.vo.CatalogVO;
import com.yuan.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;


/**
 * 分类 控制器.
 */
@Controller
@RequestMapping("/catalogs")
public class CatalogController {
	
	@Autowired
	private CatalogService catalogService;
	
	@Autowired
	private UserDetailsService userDetailsService;

	/**
	 * 获取分类列表
	 * @param username
	 * @param model
	 * @return
	 */
	@GetMapping
	public String listComments(@RequestParam(value="username",required=true) String username, Model model) {
		User user = (User)userDetailsService.loadUserByUsername(username);
		List<Catalog> catalogs = catalogService.listCatalogs(user);

		// 判断操作用户是否是分类的所有者
		boolean isOwner = false;
		
		if (SecurityContextHolder.getContext().getAuthentication() !=null 
				&& SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				 &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()
				 .equals("anonymousUser")) {
			User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal(); 
			if (principal !=null && user.getUsername().equals(principal.getUsername())) {
				isOwner = true;
			} 
		} 
		
		model.addAttribute("isCatalogsOwner", isOwner);
		model.addAttribute("catalogs", catalogs);
		return "userspace/u :: #catalogRepleace";
	}
	
	/**
	 * 创建分类
	 * @param catalogVO
	 * @return
	 */
	@PostMapping
	@PreAuthorize("authentication.name.equals(#catalogVO.username)")// 指定用户才能操作方法
	public ResponseEntity<Response> create(@RequestBody CatalogVO catalogVO) {
		
		String username = catalogVO.getUsername();
		Catalog catalog = catalogVO.getCatalog();
		
		User user = (User)userDetailsService.loadUserByUsername(username);
		
		catalog.setUser(user);
		catalogService.saveCatalog(catalog);

		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 删除分类
	 * @param username
	 * @param id
	 * @return
	 */
	@DeleteMapping("/{id}")
	@PreAuthorize("authentication.name.equals(#username)")  // 指定用户才能操作方法
	public ResponseEntity<Response> delete(String username, @PathVariable("id") Long id) {
		try {
			// 好像哪里设置的外键约束。已经有博客使用了这个分类时，就不能删除
			// 会报DataIntegrityViolationException 异常
			catalogService.removeCatalog(id);
		} catch (DataIntegrityViolationException e)  {
			return ResponseEntity.ok().body(new Response(false,"已经有博客使用该分类，不可删除！！"));
		}
		
		return ResponseEntity.ok().body(new Response(true, "处理成功", null));
	}
	
	/**
	 * 获取分类编辑界面 没有id是新增
	 */
	@GetMapping("/edit")
	public String getCatalogEdit(Model model) {
		Catalog catalog = new Catalog(null, null);
		model.addAttribute("catalog",catalog);
		return "userspace/catalogedit";
	}
	
	/**
	 * 根据 Id 获取编辑界面
	 */
	@GetMapping("/edit/{id}")
	public String getCatalogById(@PathVariable("id") Long id, Model model) {
		Optional<Catalog> optionalCatalog = catalogService.getCatalogById(id);
		Catalog catalog = null;
		
		if (optionalCatalog.isPresent()) {
			catalog = optionalCatalog.get();
		}
		
		model.addAttribute("catalog",catalog);
		return "userspace/catalogedit";
	}
}

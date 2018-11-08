package com.yuan.blog.controller;

import com.yuan.blog.domain.Authority;
import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;
import com.yuan.blog.service.AuthorityService;
import com.yuan.blog.service.BlogService;
import com.yuan.blog.service.CatalogService;
import com.yuan.blog.service.UserService;
import com.yuan.blog.util.ConstraintViolationExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 主页控制器
 */
@Controller
public class MainController {
    private static final Long ROLE_USER_AUTHORITY_ID = 2L; // 用户权限（博主）

    @Autowired
    private UserService userService;
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CatalogService catalogService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value="order",required=false,defaultValue="new") String order,
                        @RequestParam(value="categoryId",required=false ) Long categoryId,
                        @RequestParam(value="keyword",required=false,defaultValue="" ) String keyword,
                        @RequestParam(value="async",required=false) boolean async,
                        @RequestParam(value="pageIndex",required=false,defaultValue="0") int pageIndex,
                        @RequestParam(value="pageSize",required=false,defaultValue="5") int pageSize,
                        Model model) {
        User  user = (User)userDetailsService.loadUserByUsername("milo");
        Page<Blog> page = null;
        if (categoryId != null &&categoryId > 0) {
            Optional<Catalog> optionalCatalog = catalogService.getCatalogById(categoryId);
            Catalog catalog = null;
            if (optionalCatalog.isPresent()) {
                catalog = optionalCatalog.get();
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                page = blogService.listBlogsByCatalog(catalog, pageable);
                order = "";
            }
        }
        if (order.equals("new")) { // 最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            long start = System.currentTimeMillis();
            page = blogService.listBlogsByKeywordByTime( keyword, pageable);
            System.out.println("耗时：" + (System.currentTimeMillis() - start));

        }
        if (order.equals("hot")) { // 最热查询 阅读/评论/点赞量
            Sort sort = new Sort(Sort.Direction.DESC,"reading","comments","likes");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            long start = System.currentTimeMillis();
            page = blogService.listBlogsByKeywordByHot(keyword, pageable);
            System.out.println("耗时：" + (System.currentTimeMillis() - start));
        }

        List<Blog> list = page.getContent();	// 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("keyword",keyword);
        model.addAttribute("blogList", list);
        return (async?"index :: #mainContainerRepleace":"index");
    }

    /**
     * 获取登录界面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        model.addAttribute("errorMsg", "登陆失败，账号或者密码错误！");
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }
    /**
     * 注册用户
     */
    @PostMapping("/register")
    public String registerUser(User user,Model model) {
        try{
            // 明文密码加密
            user.setEncodePassword(user.getPassword());
            List<Authority> authorities = new ArrayList<>();
            authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID).get());
            user.setAuthorities(authorities);
            userService.registerUser(user);
        }catch (ConstraintViolationException e){
            // 从校验异常中提取 错误信息返回给前端
            model.addAttribute("registerError", true);
            model.addAttribute("errorMsg", ConstraintViolationExceptionHandler.getMessage(e));
            return "register";
        } catch (Exception e){
            model.addAttribute("registerError", true);
            model.addAttribute("errorMsg", e.getMessage());
            return "register";
        }
        return "redirect:/login";
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }
}

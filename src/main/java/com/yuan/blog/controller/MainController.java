package com.yuan.blog.controller;

import com.yuan.blog.domain.Authority;
import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;
import com.yuan.blog.service.*;
import com.yuan.blog.util.Cache;
import com.yuan.blog.util.NetUtil;
import com.yuan.blog.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 主页控制器
 */
@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);

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
    private SystemLogService systemLogService;

    @GetMapping("/")
    public String root() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value = "order", required = false, defaultValue = "new") String order,
                        @RequestParam(value = "categoryId", required = false) Long categoryId,
                        @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                        @RequestParam(value = "async", required = false) boolean async,
                        @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                        Model model, HttpServletRequest request) {
        log.info("visit first page : index");

        User currentUser = NetUtil.getCurrentUser(false);
//        systemLogService.insertSystemLog(request, currentUser, "首页");
        Page<Blog> page = null;
        if (categoryId != null && categoryId > 0) {
            Optional<Catalog> optionalCatalog = catalogService.getCatalogById(categoryId);
            Catalog catalog;
            if (optionalCatalog.isPresent()) {
                catalog = optionalCatalog.get();
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                page = blogService.listBlogsByCatalog(catalog, pageable);
                order = "";
            }
        }
        if (order.equals("new")) { // 最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByKeywordByTime(keyword, pageable);
        }
        if (order.equals("hot")) { // 最热查询 阅读/评论/点赞量
            Sort sort = new Sort(Sort.Direction.DESC, "reading", "comments", "likes");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByKeywordByHot(keyword, pageable);
        }
        // 是否显示记账div
        if (currentUser != null) {
            String username = currentUser.getUsername();
            Object obj = Cache.get(Cache.USER_PERFIX + username);
            if (obj != null) {
                model.addAttribute("showTalley", true);
                model.addAttribute("talleyList", blogService.collectTalley(username));
            }
        }
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("blogList", page.getContent());// 当前所在页面数据列表
        return (async ? "index :: #mainContainerRepleace" : "index");
    }

    /**
     * 获取登录界面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/insert")
    public String insert(@RequestBody User user) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        User currentUser = NetUtil.getCurrentUser(false);
        if (currentUser != null) {
            // 不能直接对原始密码加密 再和数据库的密文对比，BCryptPasswordEncoder每次加密的结果是不一样的
            if (encoder.matches(user.getPassword(), currentUser.getPassword())) {
                Cache.put(Cache.USER_PERFIX + currentUser.getUsername(), true, 30 * 1000);
            }
        }
        return "redirect:/index";
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
//    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(User user) throws Exception {
        // 明文密码加密
        user.setEncodePassword(user.getPassword());
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authorityService.getAuthorityById(ROLE_USER_AUTHORITY_ID).get());
        user.setAuthorities(authorities);
        userService.registerUser(user);
        return ResponseEntity.ok().body(new Response(true, "注册成功", user));
    }

    @GetMapping("/search")
    public String search() {
        return "search";
    }
}

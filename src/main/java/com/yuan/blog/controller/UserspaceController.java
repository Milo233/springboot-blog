package com.yuan.blog.controller;

import com.yuan.blog.domain.*;
import com.yuan.blog.service.BlogService;
import com.yuan.blog.service.CatalogService;
import com.yuan.blog.service.UserService;
import com.yuan.blog.util.ConstraintViolationExceptionHandler;
import com.yuan.blog.util.NetUtil;
import com.yuan.blog.vo.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * 用户主页空间控制器.
 */
@Controller
@RequestMapping("/u")
public class UserspaceController {

    @Autowired
    private UserService userService;
    @Resource
    private UserDetailsService userDetailsService;

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private BlogService blogService;

    private static final Logger log = LoggerFactory.getLogger(UserspaceController.class);

    /**
     * 获取用户配置
     */
    @GetMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView profile(@PathVariable("username") String username, Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/profile", "userModel", model);
    }

    /**
     * 保存个人设置
     */
    @PostMapping("/{username}/profile")
    @PreAuthorize("authentication.name.equals(#username)")
    public String saveProfile(@PathVariable("username") String username, User user) {
        User originalUser = userService.getUserById(user.getId());
        originalUser.setEmail(user.getEmail());
        originalUser.setName(user.getName());

        // 判断密码是否做了变更
        String rawPassword = originalUser.getPassword();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePasswd = encoder.encode(user.getPassword());
        boolean isMatch = encoder.matches(rawPassword, encodePasswd);
        // 页面用****** 来填充密码框，如果是****** 就不修改密码
        if (!isMatch && !"******".equals(user.getPassword())) {
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
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/avatar", "userModel", model);
    }

    /**
     * 保存头像
     */
    @PostMapping("/{username}/avatar")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveAvatar(Long id, MultipartFile file, HttpServletRequest request) {
        //  1.选择文件以后只展示到前端
        //  2.点击 提交以后再把文件丢给后端，然后后端post提交到存图的网站
        //  3.获取图片地址以后存到数据库
        String avatarUrl = NetUtil.uploadImage(request, file);
        if (avatarUrl == null || avatarUrl.length() == 0) {
            return Response.getResponse(false, "提交失败", null);
        }
        User originalUser = userService.getUserById(id);
        originalUser.setAvatar(avatarUrl);
        userService.saveOrUpdateUser(originalUser);
        return Response.getResponse(true, "提交成功", avatarUrl);
    }

    /**
     * 上传图片 返回图片地址
     */
    @PostMapping("/{username}/uploadImage")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> uploadImage(@PathVariable("username") String username,
                                                MultipartFile file, HttpServletRequest request) {
        String imageUrl = NetUtil.uploadImage(request, file);
        if (imageUrl == null || imageUrl.length() == 0) {
            return Response.getResponse(false, "提交失败", null);
        }
        return Response.getResponse(true, "提交成功", imageUrl);
    }

    // 根据用户名 获取用户信息
    @GetMapping("/{username}")
    public String userSpace(@PathVariable("username") String username, Model model) {
        //fixme 哪里掉这里了？？？ 点击个人主页后会调两次这里 第二次时 username 是 icon.ico
        if (StringUtils.isEmpty(username) || "icon.ico".equals(username)) {
            username = "milo";
        }
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);
        return "redirect:/u/" + username + "/blogs";
    }

    /**
     * 获取用户的博客列表
     */
    @GetMapping("/{username}/blogs")
    public String listBlogsByOrder(@PathVariable("username") String username,
                                   @RequestParam(value = "order", required = false, defaultValue = "new") String order,
                                   @RequestParam(value = "categoryId", required = false) Long categoryId,
                                   @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                   @RequestParam(value = "async", required = false) boolean async,
                                   @RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                   Model model) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        model.addAttribute("user", user);

        Page<Blog> page = null;
        if (categoryId != null && categoryId > 0) {
            Optional<Catalog> optionalCatalog = catalogService.getCatalogById(categoryId);
            Catalog catalog = null;
            if (optionalCatalog.isPresent()) {
                catalog = optionalCatalog.get();
                Pageable pageable = PageRequest.of(pageIndex, pageSize);
                page = blogService.listBlogsByCatalog(catalog, pageable);
                order = "";
            }
        }
        if (order.equals("hot")) { // 最热查询 阅读/评论/点赞量
            Sort sort = new Sort(Sort.Direction.DESC, "reading", "comments", "likes");
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            page = blogService.listBlogsByUserAndKeywordByHot(user, keyword, pageable);
        }
        if (order.equals("new")) { // 最新查询
            Pageable pageable = PageRequest.of(pageIndex, pageSize);
            page = blogService.listBlogsByUserAndKeywordByTime(user, keyword, pageable);
        }

        List<Blog> list = page.getContent();    // 当前所在页面数据列表

        model.addAttribute("user", user);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("order", order);
        model.addAttribute("page", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("blogList", list);
        return (async ? "userspace/u :: #mainContainerRepleace" : "userspace/u");
    }

    /**
     * 获取博客展示界面
     */
    @GetMapping("/{username}/blogs/{id}")
    public String getBlogById(@PathVariable("username") String username, @PathVariable("id") Long id,
                              Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        Blog blog = blogService.getBlogById(id);
        boolean isBlogOwner = false;
        // 判断操作用户是否是博客的所有者
        User principal = NetUtil.getCurrentUser();
        if (principal != null && username.equals(principal.getUsername())) {
            isBlogOwner = true;
        }
        // 判断是否可以查看博客 0私密博客，1开放博客
        // 公开博客，所有人可以看，加密的只有 自己or管理员 可以看
        if (blog.getOpen() == 0 && !isBlogOwner) {// 加密博客 且不是owner
            if (principal == null) return "error"; // 未登陆 直接返回error
            // 已登陆 但是不是加密博客
            if (!Authority.NAME_ADMIN.equals(principal.getFirstAuthority())) {
                return "error";
            }
        }

        // 判断操作用户的点赞情况 有的话前端展示取消，没有的话可以点赞
        List<Vote> votes = blog.getVotes(); // todo ava.lang.NullPointerException: null 某些情况下会npe
        Vote currentVote = null; // 当前用户的点赞情况

        if (principal != null) {
            for (Vote vote : votes) {
                // 从博客的vote列表里查出当前用户的点赞 vote 返回给页面
                if (vote.getUser().getUsername().equals(principal.getUsername())) {
                    currentVote = vote;
                    break;
                }
            }
        }

        // 每次读取，简单的可以认为阅读量增加1次
        blogService.readingIncrease(id);

        model.addAttribute("isBlogOwner", isBlogOwner);
        model.addAttribute("blogModel", blogService.getBlogById(id));
        model.addAttribute("currentVote", currentVote);
        model.addAttribute("keyword", keyword);
        return "userspace/blog";
    }

    /**
     * 获取新增博客的界面
     */
    @GetMapping("/{username}/blogs/edit")
    public ModelAndView createBlog(@PathVariable("username") String username, Model model) {
        // 获取用户分类列表
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);

        model.addAttribute("catalogs", catalogs);
        model.addAttribute("blog", new Blog(null, null, null));
        model.addAttribute("username", username);
        model.addAttribute("user", user);
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    /**
     * 获取 update 博客的界面
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ModelAndView editBlog(@PathVariable("username") String username,
                                 @PathVariable("id") Long id, Model model) {
        // 获取用户分类列表
        User user = (User) userDetailsService.loadUserByUsername(username);
        List<Catalog> catalogs = catalogService.listCatalogs(user);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("catalogs", catalogs);
        model.addAttribute("blog", blogService.getBlogById(id));
        return new ModelAndView("userspace/blogedit", "blogModel", model);
    }

    /**
     * 保存博客
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> saveBlog(@PathVariable("username") String username, @RequestBody Blog blog) {
        // 对 Catalog 进行空处理
        if (blog.getCatalog().getId() == null) {
            return ResponseEntity.ok().body(new Response(false, "未选择分类"));
        }

        User user = (User) userDetailsService.loadUserByUsername(username);
        blog.setUser(user);
        try {
            // 判断是修改还是新增
            if (blog.getId() != null) {
                Blog exitBlog = blogService.getBlogById(blog.getId());
                if (exitBlog != null) {
                    exitBlog.setTitle(blog.getTitle());
                    exitBlog.setContent(blog.getContent());
                    exitBlog.setHtmlContent(blog.getHtmlContent());
                    exitBlog.setSummary(blog.getSummary());
                    exitBlog.setCatalog(blog.getCatalog()); // 增加对分类的处理
                    exitBlog.setTags(blog.getTags());  // 增加对标签的处理
                    exitBlog.setOpen(blog.getOpen());
                    blogService.saveBlog(exitBlog);
                }
            } else {
                blog.setUser(user);
                blogService.saveBlog(blog);
            }
        } catch (ConstraintViolationException e) {
            return ResponseEntity.ok().body(new Response(false, ConstraintViolationExceptionHandler.getMessage(e)));
        } catch (Exception e) {
            return Response.getResponse(false, e.getMessage(), null);
        }

        String redirectUrl = "/u/" + username + "/blogs/" + blog.getId();
        return Response.getResponse(true, "处理成功", redirectUrl);
    }

    /**
     * 删除博客
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public ResponseEntity<Response> deleteBlog(@PathVariable("username") String username, @PathVariable("id") Long id) {

        blogService.removeBlog(id);

        String redirectUrl = "/u/" + username + "/blogs";
        return ResponseEntity.ok().body(new Response(true, "处理成功", redirectUrl));
    }

    /**
     * 插入记账记录
     */
    @PostMapping("/{username}/tally/save")
    public ResponseEntity<Response> saveTally(@PathVariable("username") String username, @RequestBody List<Tally> tallyList) {
        User user = (User) userDetailsService.loadUserByUsername(username);
        blogService.saveTally(tallyList, user);
        return ResponseEntity.ok().body(new Response(true, "保存记账记录成功", user));
    }
}

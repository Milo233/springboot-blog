package com.yuan.blog.controller;

import com.yuan.blog.domain.Blog;
import com.yuan.blog.domain.Comment;
import com.yuan.blog.domain.User;
import com.yuan.blog.response.Result;
import com.yuan.blog.service.BlogService;
import com.yuan.blog.service.CommentService;
import com.yuan.blog.util.NetUtil;
import com.yuan.blog.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 主页控制器.
 */
@Controller
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private CommentService commentService;

    /**
     * 获取评论列表
     */
    @GetMapping
    public String listComments(@RequestParam(value = "blogId") Long blogId, Model model) {
        Blog blog = blogService.getBlogById(blogId);
        List<Comment> comments = blog.getComments();

        // 判断操作用户是否是评论的所有者
        String commentOwner = "";
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null) {
                commentOwner = principal.getUsername();
            }
        }

        model.addAttribute("commentOwner", commentOwner);
        model.addAttribute("comments", comments);
        return "userspace/blog :: #mainContainerRepleace";
    }

    /**
     * 发表评论
     *  fixme 结果封装修改的话 要同步修改 ExceptionHandlerAdvice的结果封装
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    @ResponseBody
    public Result<Boolean> createComment2(Long blogId, String commentContent) {
        User currentUser = NetUtil.getCurrentUser(false);
        Long userId = 0L;
        if (currentUser != null) {
            userId = currentUser.getId();
        }
        blogService.createComment(blogId, commentContent, userId);
        // return Result.error(CodeMsg.SESSION_ERROR);{"code":500210,"msg":"Session不存在或者已经失效","data":null}
        // Result.success(currentUser) {"code":0,"msg":null,"data":{"id":1,"name":"milo","email":"748561384@qq.com","username":"milo"} 查询单个对象. 查询结果都可以丢进去
        // List 多个 {"code":0,"msg":null,"data":[{"id":1,"name":"milo","email":"748561384@qq.com","username":"milo"}]}
        return Result.success(true);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteBlog(@PathVariable("id") Long id, Long blogId) {
        boolean isOwner = false;
        User user = commentService.getCommentById(id).getUser();

        // 判断操作用户是否是博客的所有者
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal != null && user.getUsername().equals(principal.getUsername())) {
                isOwner = true;
            }
        }

        if (!isOwner) {
            return ResponseEntity.ok().body(new Response(false, "没有操作权限"));
        }
        blogService.removeComment(id);
        return ResponseEntity.ok().body(new Response(true, "处理成功", null));
    }
}

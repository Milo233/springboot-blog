package com.yuan.blog.controller;

import com.yuan.blog.vo.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 点赞控制器.
 */
@Controller
@RequestMapping("/votes")
public class VoteController {

    /**
     * 发表点赞
     * @return 点赞成功返回vote 的id 用于前端展示
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")//验证已登录
//	@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> createVote(Long blogId) {
        return ResponseEntity.ok().body(new Response(true, "点赞成功", 1));
    }

    /**
     * 删除点赞
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_USER')")  // 指定角色权限才能操作方法
    public ResponseEntity<Response> delete(@PathVariable("id") Long id, Long blogId) {
        return ResponseEntity.ok().body(new Response(true, "取消点赞成功", null));
    }
}
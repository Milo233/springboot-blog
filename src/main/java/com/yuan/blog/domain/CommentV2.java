package com.yuan.blog.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Comment 实体.
 */
public class CommentV2 implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id; // 用户的唯一标识

    private String content;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    private User user;

    private Timestamp createTime;

    public CommentV2(Long userId, String content) {
        this.content = content;
        this.userId = userId;
    }

    public CommentV2(User user, String content) {
        this.content = content;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }
}

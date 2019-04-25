package com.yuan.blog.response;

import com.yuan.blog.vo.Todo;

public class TodoResponse extends Todo {

    // 创建者的邮箱，用于通知
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

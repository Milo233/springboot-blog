package com.yuan.blog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
public class HelloController {
    @RequestMapping("/hello")
    public String hello(){
        System.out.println("get hello running!!!");
        return "hello world!你好啊你好哇";
    }
}

package com.yuan.blog.listener;

import com.yuan.blog.service.InitBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
/**
 * 初始化博客监听器.
 * 监听Spring容器启动完成事件，初始化权限、初始化管理员
 */
@Component
public class InitBlogListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private InitBlogService initBlogService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initBlogService.initAdmin();
    }
}

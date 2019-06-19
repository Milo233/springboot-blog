package com.yuan.blog.controller;

import com.yuan.blog.vo.Menu;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台管理主控制器.
 */
@Controller
@RequestMapping("/admins")
public class AdminController implements InitializingBean {

    /**
     * 获取后台管理主页面
     */
    @GetMapping
    public ModelAndView listUsers(Model model) {
        List<Menu> list = new ArrayList<>();
        list.add(new Menu("用户管理", "/users"));
        list.add(new Menu("角色管理", "/roles"));
        list.add(new Menu("博客管理", "/blogs"));
        list.add(new Menu("评论管理", "/commits"));
        model.addAttribute("list", list);
        return new ModelAndView("admins/index", "model", model);
    }

    /**
     * 类实例被创建，属性都被设置以后会调用这个方法
     * 可用于做初始化操作
     */
    @Override
    public void afterPropertiesSet() {
        System.out.println("method of afterPropertiesSet....");
    }
}

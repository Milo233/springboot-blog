package com.yuan.blog.service;

import com.yuan.blog.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
@Repository
public class UserServiceImpl {
//public class UserServiceImpl implements UserService {
    // 原来是实现UserService 要重写service方法。改成jpa的以后有自动实现的基础方法
    /*private static AtomicLong counter = new AtomicLong();

    private final ConcurrentMap<Long, User> userMap = new ConcurrentHashMap<Long, User>();

    public UserServiceImpl(){
        // 创建默认值
//        User user = new User();
//        user.setAge(30);
//        user.setName("milo");
//        this.saveOrUpateUser(user);
    }

    *//* (non-Javadoc)
     * @see com.waylau.spring.boot.thymeleaf.repository.UserRepository#saveUser(com.waylau.spring.boot.thymeleaf.vo.UserVO)
     *//*
    @Override
    public User saveOrUpateUser(User user) {
        Long id = user.getId();
        if (id <= 0) {
            id = counter.incrementAndGet();
            user.setId(id);
        }
        this.userMap.put(id, user);
        return user;
    }

    *//* (non-Javadoc)
     * @see com.waylau.spring.boot.thymeleaf.repository.UserRepository#deleteUser(java.lang.Long)
     *//*
    @Override
    public void deleteUser(Long id) {
        this.userMap.remove(id);
    }

    *//* (non-Javadoc)
     * @see com.waylau.spring.boot.thymeleaf.repository.UserRepository#getUserById(java.lang.Long)
     *//*
    @Override
    public User getUserById(Long id) {
        return this.userMap.get(id);
    }

    *//* (non-Javadoc)
     * @see com.waylau.spring.boot.thymeleaf.repository.UserRepository#listUser()
     *//*
    @Override
    public List<User> listUser() {
        return new ArrayList<User>(this.userMap.values());
    }*/
}

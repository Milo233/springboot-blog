package com.yuan.blog.service;

import com.yuan.blog.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface UserService {

    /**
     * 保存用户/更新用户
     */
    User saveOrUpdateUser(User user);

    /**
     *
      */
    User registerUser(User user);

    /**
     * 删除用户
     */
    void removeUser(Long id);

    /**
     * 删除列表里面的用户
     */
    void removeUsersInBatch(List<User> users);


    /**
     * 根据id获取用户
     */
    User getUserById(Long id);

    /**
     * 获取用户列表
     */
    List<User> listUsers();

    /**
     * 根据用户名进行分页模糊查询
     */
    Page<User> listUsersByNameLike(String name, Pageable pageable);

    /**
     * 根据用户名集合，查询用户详细信息列表
     * @param usernames
     * @return
     */
    List<User> listUsersByUsernames(Collection<String> usernames);
}

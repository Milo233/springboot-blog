package com.yuan.blog.service;

import com.yuan.blog.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserService extends CrudRepository<User, Long> {

//    /**
//     * 新增或者修改用户
//     * @param user
//     * @return
//     */
//    User saveOrUpateUser(User user);
//
//    /**
//     * 删除用户
//     * @param id
//     */
//    void deleteUser(Long id);
//
//    /**
//     * 根据用户id获取用户
//     * @param id
//     * @return
//     */
//    User getUserById(Long id);
//
//    /**
//     * 获取所有用户的列表
//     * @return
//     */
//    List<User> listUser();
}

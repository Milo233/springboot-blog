package com.yuan.blog.dao;

import com.yuan.blog.domain.Catalog;
import com.yuan.blog.domain.User;

import java.util.List;

public interface CatalogDao {

    /**
     * 根据用户查询
     * @param user
     * @return
     */
    List<Catalog> findByUser(User user);

    /**
     * 根据用户、分类名称查询
     * @param user
     * @param name
     * @return
     */
    List<Catalog> findByUserAndName(User user, String name);


}

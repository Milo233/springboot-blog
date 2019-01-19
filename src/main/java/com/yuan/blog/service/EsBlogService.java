package com.yuan.blog.service;

import com.yuan.blog.domain.EsBlog;
import com.yuan.blog.domain.User;
import com.yuan.blog.vo.TagVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by milo on 2018/9/22.
 */  // 继承ElasticsearchRepository 用于全文搜索
    // public interface EsBlogService extends ElasticsearchRepository<EsBlog,String>
    // sql 自动根据方法名生成 todo 研究下这里的生成规则 以及 实现原理？
public interface EsBlogService {
    /**
     * 删除EsBlog
     * @param id
     * @return
     */
    void removeEsBlog(String id);

    /**
     * 更新 EsBlog
     */
    EsBlog updateEsBlog(EsBlog esBlog);

    /**
     * 根据Blog的Id获取EsBlog
     */
    EsBlog getEsBlogByBlogId(Long blogId);

    /**
     * 最新博客列表，分页
     */
    Page<EsBlog> listNewestEsBlogs(String keyword, Pageable pageable);

    /**
     * 最热博客列表，分页
     */
    Page<EsBlog> listHotestEsBlogs(String keyword, Pageable pageable);

    /**
     * 博客列表，分页
     */
    Page<EsBlog> listEsBlogs(Pageable pageable);
    /**
     * 最新前5
     */
    List<EsBlog> listTop5NewestEsBlogs();

    /**
     * 最热前5
     */
    List<EsBlog> listTop5HotestEsBlogs();

    /**
     * 最热前 30 标签
     */
    List<TagVO> listTop30Tags();

    /**
     * 最热前12用户
     */
    List<User> listTop12Users();
}

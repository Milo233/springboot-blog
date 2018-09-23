package com.yuan.blog.service;

import com.yuan.blog.domain.EsBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by milo on 2018/9/22.
 */  // 继承ElasticsearchRepository 用于全文搜索
public interface EsBlogService extends ElasticsearchRepository<EsBlog,String> {
    // sql 自动根据方法名生成 todo 研究下这里的生成规则 以及 实现原理？
    Page<EsBlog> findBySummaryLikeOrContentLike(String summary, String content, Pageable pageable);

}

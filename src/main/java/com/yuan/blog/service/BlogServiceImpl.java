package com.yuan.blog.service;

import com.yuan.blog.dao.BlogDao;
import com.yuan.blog.domain.*;
import com.yuan.blog.repository.BlogRepository;
import com.yuan.blog.vo.TallyCategoryEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Blog 服务.
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;
    @Resource
    private BlogDao blogDao;

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public void removeBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Blog updateBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    public Blog getBlogById(Long id) {
        Optional<Blog> byId = blogRepository.findById(id);
        Blog blog = null;
        if (byId.isPresent()) {
            blog = byId.get();
        }
        return blog;
    }

    @Override
    public Page<Blog> listBlogsByUserAndKeywordByTime(User user, String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        // 按时间先后查询
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return blogRepository.findByUserAndTitleLikeOrUserAndTagsLikeOrUserAndContentLike(user, title, user,
                title, user, title, pageable);
    }

    /**
     * v 1.0
     * 查最新的的blog
     */
    @Override
    public Page<Blog> listBlogsByKeywordByTime(String title, Pageable pageable) {
        // 模糊查询
        title = "%" + title + "%";
        Page<Blog> ll = ll(title);
        // 按时间先后查询
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return blogRepository.findByTitleLikeOrTagsLikeOrContentLike(title, title, title, pageable);
    }

    /**
     * v 2.0
     * 查最新的的blog
     * 1.按时间排序 2.权限 3.分页
     */
    @Override
    public Page<Blog> ll(String keyword) {


        HashMap<String, Object> map = new HashMap<>();
        map.put("loginId", 2);
        map.put("keyword", keyword);
        map.put("orderBy", "hot");

        // 分页是从1开始的
//		PageHelper.startPage(1, 10,true);
//		List<Blog> blogs = blogDao.queryList(map);
//		PageInfo<Blog> pageInfo = new PageInfo<>(blogs);
        return null;
    }


    @Override
    public Page<Blog> listBlogsByKeywordByHot(String keyword, Pageable pageable) {
        String title = "%" + keyword + "%";
        // 按根据点赞量 阅读量 创建时间作为热度查询
        Sort sort = new Sort(Sort.Direction.DESC, "readSize", "voteSize", "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return blogRepository.findByTitleLikeOrTagsLikeOrContentLike(title, title, title, pageable);
    }

    @Override
    public Page<Blog> listBlogsByUserAndKeywordByHot(User user, String keyword, Pageable pageable) {
        String title = "%" + keyword + "%";
        // 按根据点赞量 阅读量 创建时间作为热度查询
        Sort sort = new Sort(Sort.Direction.DESC, "readSize", "voteSize", "createTime");
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return blogRepository.findByUserAndTitleLikeOrUserAndTagsLikeOrUserAndContentLike(user, title, user, title, user, title, pageable);
    }

    /**
     * 阅读量递增
     */
    @Override
    public void readingIncrease(Long id) {
        Optional<Blog> byId = blogRepository.findById(id);
        if (byId.isPresent()) {
            Blog blog = byId.get();
            blog.setReadSize(blog.getReadSize() + 1);
            blogRepository.save(blog);
        }
    }

    @Override
    @Transactional // 只读的不加事务注解就好了
    public int createComment(Long blogId, String commentContent, Long userId) {
        // 废除hibernate的写法。改成直接新增一条 一条comment 一条blog_comment中间表
//        Blog originalBlog = blogRepository.findById(blogId).get();
        CommentV2 comment = new CommentV2(userId, commentContent);
        blogDao.createComment(comment);
        Map<String, Object> blogCommentMap = new HashMap<>();
        blogCommentMap.put("blogId", blogId);
        blogCommentMap.put("commentId", comment.getId());
        return blogDao.insertBlogComment(blogCommentMap);
    }

    @Override
    @Transactional
    public void removeComment(Long commentId) {
        // 移除comment 和 blog_comment
        blogDao.deleteBlogComment(commentId);
        blogDao.deleteComment(commentId);
    }

    @Override
    public Blog createVote(Long blogId) {
        // 判断用户是否点过赞
        Blog originalBlog = blogRepository.findById(blogId).get();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Vote vote = new Vote(user);
        boolean isExist = originalBlog.addVote(vote);
        if (isExist) {
            throw new IllegalArgumentException("该用户已经点过赞了");
        }
        return blogRepository.save(originalBlog);
    }

    @Override
    public void removeVote(Long blogId, Long voteId) {
        Blog originalBlog = blogRepository.findById(blogId).get();
        originalBlog.removeVote(voteId);
        blogRepository.save(originalBlog);
    }


    @Override
    public Page<Blog> listBlogsByCatalog(Catalog catalog, Pageable pageable) {
        return blogRepository.findByCatalog(catalog, pageable);
    }

    @Override
    public void saveTally(List<Tally> tallyList, User user) {
        if (user == null) {
            return;
        }
        String userName = user.getUsername();
        Date now = new Date();
        for (Tally tally : tallyList) {
            tally.setUserName(userName);
            tally.setCreateTime(now);
            int type = TallyCategoryEnum.explainType(tally.getCategoryId() + "");
            tally.setType(type);
            tally.setAmount(tally.getAmount().multiply(new BigDecimal(type)));
            blogDao.saveTally(tally);
        }
    }

    @Override
    public int updateWord(String content, int id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("id", 1);
        return blogDao.updateWord(map);
    }

    @Override
    public String getContentById(int id) {
        return blogDao.getContentById(id);
    }

    @Override
    public List<TalleyCollection> collectTalley(String userName) {
        return blogDao.collectTalley(userName);
    }
}

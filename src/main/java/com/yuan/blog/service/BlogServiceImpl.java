package com.yuan.blog.service;

import com.yuan.blog.domain.*;
import com.yuan.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Blog 服务.
 */
@Service
public class BlogServiceImpl implements BlogService {

	@Autowired
	private BlogRepository blogRepository;

	@Transactional
	@Override
	public Blog saveBlog(Blog blog) {
		boolean isNew = (blog.getId() == null);
		Blog returnBlog = blogRepository.save(blog);

		// 存到数据库以后还要存到 ES里 用于全文检索
/*		EsBlog esBlog = null;
		if (isNew) {
			// 从Blog对象 生成一个 EsBlog对象。id不一定一样
			esBlog = new EsBlog(returnBlog);
		} else {
			esBlog = esBlogService.getEsBlogByBlogId(blog.getId());
			esBlog.update(returnBlog);
		}

		esBlogService.updateEsBlog(esBlog);*/
		return returnBlog;
	}

	@Transactional
	@Override
	public void removeBlog(Long id) {
		blogRepository.deleteById(id);
		// 删除es
		//EsBlog esblog = esBlogService.getEsBlogByBlogId(id);
		//esBlogService.removeEsBlog(esblog.getId());
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
		if(byId.isPresent()){
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
		Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrUserAndTagsLikeOrUserAndContentLike(user, title,user,
				title,user, title, pageable);
		return blogs;
	}

	@Override
	public Page<Blog> listBlogsByKeywordByTime(String title, Pageable pageable) {
		// 模糊查询
		title = "%" + title + "%";
		// 按时间先后查询
		Sort sort = new Sort(Sort.Direction.DESC, "createTime");
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		Page<Blog> blogs = blogRepository.findByTitleLikeOrTagsLikeOrContentLike(title, title, title, pageable);
		return blogs;
	}

	@Override
	public Page<Blog> listBlogsByKeywordByHot( String keyword, Pageable pageable) {
		String title = "%" + keyword + "%";
		String tag = title;
		// 按根据点赞量 阅读量 创建时间作为热度查询
		Sort sort = new Sort(Sort.Direction.DESC, "readSize", "voteSize", "createTime");
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		Page<Blog> blogs = blogRepository.findByTitleLikeOrTagsLikeOrContentLike(title, tag,title,pageable);
		return blogs;
	}

	@Override
	public Page<Blog> listBlogsByUserAndKeywordByHot(User user, String keyword, Pageable pageable) {
		String title = "%" + keyword + "%";
		String tag = title;
		// 按根据点赞量 阅读量 创建时间作为热度查询
		Sort sort = new Sort(Sort.Direction.DESC, "readSize", "voteSize", "createTime");
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
		Page<Blog> blogs = blogRepository.findByUserAndTitleLikeOrUserAndTagsLikeOrUserAndContentLike(user, title, user, tag,
				user, title, pageable);
		return blogs;
	}

	/**阅读量递增 */
	@Override
	public void readingIncrease(Long id) {
		try{
			Optional<Blog> byId = blogRepository.findById(id);
			if(byId != null && byId.isPresent()){
				Blog blog =byId.get();
				blog.setReadSize(blog.getReadSize()+1);
				blogRepository.save(blog);
			}
		}catch (Exception e){
			System.out.println(e.getMessage());
		}
	}


	@Override
	public Blog createComment(Long blogId, String commentContent) {
		Blog originalBlog = blogRepository.findById(blogId).get();
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Comment comment = new Comment(user, commentContent);
		originalBlog.addComment(comment);
		return blogRepository.save(originalBlog);
	}

	@Override
	public void removeComment(Long blogId, Long commentId) {
		Blog originalBlog = blogRepository.findById(blogId).get();
		originalBlog.removeComment(commentId);
		blogRepository.save(originalBlog);
	}

	@Override
	public Blog createVote(Long blogId) {
		// 判断用户是否点过赞
		Blog originalBlog = blogRepository.findById(blogId).get();
		User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
		Page<Blog> blogs = blogRepository.findByCatalog(catalog, pageable);
		return blogs;
	}
}

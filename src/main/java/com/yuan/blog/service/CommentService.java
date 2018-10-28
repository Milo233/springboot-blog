package com.yuan.blog.service;


import com.yuan.blog.domain.Comment;

/**
 * Comment 服务接口.
 */
public interface CommentService {
	/**
	 * 根据id获取 Comment
	 * @param id
	 * @return
	 */
	Comment getCommentById(Long id);
	/**
	 * 删除评论
	 * @param id
	 * @return
	 */
	void removeComment(Long id);
}

package com.yuan.blog.service;

import com.yuan.blog.domain.Comment;
import com.yuan.blog.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Comment 服务.
 */
@Service
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Override
	@Transactional
	public void removeComment(Long id) {
		commentRepository.deleteById(id);
	}

	@Override
	public Comment getCommentById(Long id) {
		return commentRepository.findById(id).get();
	}

}

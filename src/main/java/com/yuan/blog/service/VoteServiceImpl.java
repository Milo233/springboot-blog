package com.yuan.blog.service;

import com.yuan.blog.domain.Vote;
import com.yuan.blog.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Vote getVoteById(Long id) {
        return voteRepository.findById(id).get();
    }

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }
}

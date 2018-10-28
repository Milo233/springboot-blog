package com.yuan.blog.service;

import com.yuan.blog.domain.Vote;

public interface VoteService {

    Vote getVoteById(Long id);

    void removeVote(Long id);
}

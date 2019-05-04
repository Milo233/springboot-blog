package com.yuan.blog.service;

import com.yuan.blog.domain.Vote;
import com.yuan.blog.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Override
    public Vote getVoteById(Long id) {
        Optional<Vote> byId = voteRepository.findById(id);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            return null;
        }
    }

    @Override
    public void removeVote(Long id) {
        voteRepository.deleteById(id);
    }
}

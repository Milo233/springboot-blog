package com.yuan.blog.repository;

import com.yuan.blog.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote,Long> {
                                        // 实体 主键类型
}

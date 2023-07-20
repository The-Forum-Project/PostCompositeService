package com.example.postcompositeservice.dao;

import com.example.postcompositeservice.domain.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserId(Long userId);
}

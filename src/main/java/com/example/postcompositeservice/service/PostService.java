package com.example.postcompositeservice.service;

import com.example.postcompositeservice.dao.PostRepository;
import com.example.postcompositeservice.domain.Post;
import com.example.postcompositeservice.exception.InvalidAuthorityException;
import com.example.postcompositeservice.exception.PostNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public Post getPostById(String id) {
        return postRepository.findById(id).orElse(null);
    }
    public void modifyPost(String postId, String title, String content, List<String> attachmentUrls, List<String> iamgeUrls, Long userId) throws InvalidAuthorityException, PostNotFoundException {

        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            if(post.getUserId() != userId){
                throw new InvalidAuthorityException();
            }
            if (title != null) {
                post.setTitle(title);
            }
            if (content != null) {
                post.setContent(content);
            }
            if (iamgeUrls != null) {
                //System.out.println("iamgeUrls: " + iamgeUrls);
                post.setImages(iamgeUrls);
            }
            if (attachmentUrls != null) {
                //System.out.println("attachmentUrls: " + attachmentUrls);
                post.setAttachments(attachmentUrls);
            }

            post.setDateModified(new Date());
            postRepository.save(post);
        } else {
            throw new PostNotFoundException();
        }

    }
}
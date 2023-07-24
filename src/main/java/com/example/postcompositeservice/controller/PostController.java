package com.example.postcompositeservice.controller;

import com.example.postcompositeservice.domain.Post;
import com.example.postcompositeservice.dto.FileUrlResponse;
import com.example.postcompositeservice.dto.GeneralResponse;
import com.example.postcompositeservice.dto.HistoryRequest;
import com.example.postcompositeservice.dto.PostResponse;
import com.example.postcompositeservice.exception.InvalidAuthorityException;
import com.example.postcompositeservice.exception.PostNotFoundException;
import com.example.postcompositeservice.service.PostService;
import com.example.postcompositeservice.service.remote.FileService;
import com.example.postcompositeservice.service.remote.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class PostController {
    private PostService postService;
    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(value = "/posts")
    public ResponseEntity<GeneralResponse> createPost(
            @RequestParam(value = "title")  String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "status") @NotBlank @Valid String status,
            @RequestParam(value = "images", required = false)  MultipartFile[] images,
            @RequestParam(value = "attachments", required = false)  MultipartFile[] attachments) throws InvalidAuthorityException {

        postService.savePost(title, content, status, images, attachments);
        return ResponseEntity.ok(GeneralResponse.builder().statusCode("200").message("Post created.").build());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<GeneralResponse> modifyPost(@PathVariable String postId,
                                                      @RequestParam(value = "title") @NotBlank @Valid String title,
                                                      @RequestParam(value = "content") @NotBlank @Valid String content,
                                                      @RequestParam(value = "images", required = false)  MultipartFile[] images,
                                                      @RequestParam(value = "attachments", required = false)  MultipartFile[] attachments) throws PostNotFoundException, InvalidAuthorityException {
        postService.modifyPost(postId,title, content, attachments, images);
        return ResponseEntity.ok(GeneralResponse.builder().statusCode("200").message("Post modified").build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String postId) {
        Post post = postService.getPostById(postId);
        return ResponseEntity.ok(PostResponse.builder().post(post).build());
    }
}

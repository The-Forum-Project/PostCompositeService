package com.example.postcompositeservice.controller;

import com.example.postcompositeservice.dto.*;
import com.example.postcompositeservice.exception.InvalidAuthorityException;
import com.example.postcompositeservice.exception.PostNotFoundException;
import com.example.postcompositeservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
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

    @GetMapping("/histories")
    public List<HistoryAndPostResponse> getAllHistoriesByUserId(){
        List<HistoryAndPostResponse> res = postService.getAllHistoriesByUserId();
        return res;
    }
}

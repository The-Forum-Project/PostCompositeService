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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class PostController {
    private PostService postService;
    private FileService fileService;
    private HistoryService historyService;
    @Autowired
    public void setPostService(PostService postService) {
        this.postService = postService;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping(value = "/posts")
    public ResponseEntity<GeneralResponse> createPost(
            @RequestParam(value = "title") String title,
            @RequestParam(value = "content") String content,
            @RequestParam(value = "images") MultipartFile[] images,
            @RequestParam(value = "attachments") MultipartFile[] attachments) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();

        Post post = Post.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .isArchived(false)
                .status("published")
                .dateCreated(new Date())
                .dateModified(new Date())
                .postReplies(new ArrayList<>())
                .build();

        //upload any images to S3
        System.out.println("Build post: " + post);
        ResponseEntity<FileUrlResponse> response = fileService.uploadFiles(images);
        System.out.println("image urls: " + response.getBody().getUrls());
        post.setImages(response.getBody().getUrls());

        //upload any attachments to S3
        ResponseEntity<FileUrlResponse> attachmentResponse = fileService.uploadFiles(attachments);
        System.out.println("attachment urls: " + attachmentResponse.getBody().getUrls());
        post.setAttachments(attachmentResponse.getBody().getUrls());

        postService.savePost(post);
        return ResponseEntity.ok(GeneralResponse.builder().statusCode("200").message("Post created.").build());
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<GeneralResponse> modifyPost(@PathVariable String postId, @RequestParam(value = "title") String title,
                                                      @RequestParam(value = "content") String content,
                                                      @RequestParam(value = "images") MultipartFile[] images,
                                                      @RequestParam(value = "attachments") MultipartFile[] attachments) throws PostNotFoundException, InvalidAuthorityException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<String> iamgeUrls = null;
        if(images != null && images.length > 0){
            ResponseEntity<FileUrlResponse> imagesresponse  = fileService.uploadFiles(images);
            iamgeUrls = imagesresponse.getBody().getUrls();
        }


        List<String> attachmentUrls = null;
        if(attachments != null && attachments.length > 0){
            ResponseEntity<FileUrlResponse> attachmentResponse = fileService.uploadFiles(attachments);
            attachmentUrls = attachmentResponse.getBody().getUrls();
        }


        postService.modifyPost(postId,title
                , content, attachmentUrls, iamgeUrls, userId);
        return ResponseEntity.ok(GeneralResponse.builder().statusCode("200").message("Post modified").build());
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable String postId) throws PostNotFoundException, InvalidAuthorityException {
        //need to check this user has the authority to see this post
        Post post = postService.getPostById(postId);

        //set user history
        historyService.setHistory(new HistoryRequest().builder().postId(postId).build());
        return ResponseEntity.ok(PostResponse.builder().post(post).build());
    }
}

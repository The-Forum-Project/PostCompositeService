package com.example.postcompositeservice.service;

import com.example.postcompositeservice.dto.*;
import com.example.postcompositeservice.exception.InvalidAuthorityException;
import com.example.postcompositeservice.exception.PostNotFoundException;
import com.example.postcompositeservice.service.remote.FileService;
import com.example.postcompositeservice.service.remote.HistoryService;
import com.example.postcompositeservice.service.remote.PostRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class PostService {
    private PostRemoteService postRemoteService;
    private FileService fileService;
    private HistoryService historyService;

    @Autowired
    public void setPostRemoteService(PostRemoteService postRemoteService) {
        this.postRemoteService = postRemoteService;
    }

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public void savePost(String title, String content, String status, MultipartFile[] images, MultipartFile[] attachments) throws InvalidAuthorityException {
        if (Objects.equals(status, "published") && (title.length() == 0 || content.length() == 0)) {
            throw new IllegalArgumentException("Title and content cannot be empty when publishing a post.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (Long) authentication.getPrincipal();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        if (authorities.stream().noneMatch(authority -> authority.getAuthority().equals("normal"))) {
            throw new InvalidAuthorityException();
        }
        Post post = Post.builder()
                .userId(userId)
                .title(title)
                .content(content)
                .isArchived(false)
                .status(status)
                .dateCreated(new Date())
                .dateModified(new Date())
                .postReplies(new ArrayList<>())
                .build();

        //upload any images to S3
        System.out.println("Build post: " + post);
        if(images != null){
            ResponseEntity<FileUrlResponse> response = fileService.uploadFiles(images);
            System.out.println("image urls: " + response.getBody().getUrls());
            post.setImages(response.getBody().getUrls());
        }

        //upload any attachments to S3
        if(attachments != null) {
            ResponseEntity<FileUrlResponse> attachmentResponse = fileService.uploadFiles(attachments);
            System.out.println("attachment urls: " + attachmentResponse.getBody().getUrls());
            post.setAttachments(attachmentResponse.getBody().getUrls());
        }

        postRemoteService.setPost(post);
    }

    public Post getPostById(String id) {
        PostResponse response = postRemoteService.getPostById(id);
        //set user history
        historyService.setHistory(HistoryRequest.builder().postId(id).build());
        return response.getPost();
    }

    public void modifyPost(String postId, String title, String content, MultipartFile[] attachments,  MultipartFile[] images) throws InvalidAuthorityException, PostNotFoundException {
        if (title.length() == 0 || content.length() == 0) {
            throw new IllegalArgumentException("Title and content cannot be empty when publishing a post.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        Long userId = (Long) authentication.getPrincipal();
        if (authorities.stream().noneMatch(authority -> authority.getAuthority().equals("normal"))) {
            throw new InvalidAuthorityException();
        }
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

        PostResponse response = postRemoteService.getPostById(postId);

        if (response.getPost() != null) {
            Post post = response.getPost();
            if(post.getUserId() != userId){
                throw new InvalidAuthorityException();
            }
            post.setTitle(title);
            post.setContent(content);
            if (iamgeUrls != null) {
                post.setImages(iamgeUrls);
            }
            if (attachmentUrls != null) {
                post.setAttachments(attachmentUrls);
            }

            post.setDateModified(new Date());
            postRemoteService.setPost(post);
        } else {
            throw new PostNotFoundException();
        }

    }

    public List<HistoryAndPostResponse> getAllHistoriesByUserId() {
        List<History> res = historyService.getAllHistoriesByUserId();
        List<HistoryAndPostResponse> response = new ArrayList<>();
        for (History history : res) {
            PostResponse postResponse = postRemoteService.getPostById(history.getPostId());
            if (postResponse.getPost() != null) {
                response.add(HistoryAndPostResponse.builder()
                        .history(history)
                        .post(postResponse.getPost())
                        .build());
            }
        }
        return response;
    }
}
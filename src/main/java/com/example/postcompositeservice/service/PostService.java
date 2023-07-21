package com.example.postcompositeservice.service;

import com.example.postcompositeservice.domain.Post;
import com.example.postcompositeservice.dto.FileUrlResponse;
import com.example.postcompositeservice.dto.HistoryRequest;
import com.example.postcompositeservice.dto.PostResponse;
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

    public void savePost(String title, String content, Long userId, MultipartFile[] images, MultipartFile[] attachments) throws InvalidAuthorityException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
        if (authorities.stream().noneMatch(authority -> authority.getAuthority().equals("normal"))) {
            throw new InvalidAuthorityException();
        }
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

        postRemoteService.setPost(post);
    }

    public Post getPostById(String id) {
        PostResponse response = postRemoteService.getPostById(id);
        //set user history
        historyService.setHistory(HistoryRequest.builder().postId(id).build());
        return response.getPost();
    }

    public void modifyPost(String postId, String title, String content, MultipartFile[] attachments,  MultipartFile[] images, Long userId) throws InvalidAuthorityException, PostNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();
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
            postRemoteService.setPost(post);
        } else {
            throw new PostNotFoundException();
        }

    }
}
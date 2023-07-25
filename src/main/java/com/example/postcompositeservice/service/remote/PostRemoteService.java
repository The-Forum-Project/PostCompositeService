package com.example.postcompositeservice.service.remote;

import com.example.postcompositeservice.dto.Post;
import com.example.postcompositeservice.dto.GeneralResponse;
import com.example.postcompositeservice.dto.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient("post-reply-service")
public interface PostRemoteService {
    @PostMapping(value = "post-reply-service/posts")
    GeneralResponse setPost(@RequestBody Post req);

    @GetMapping(value = "post-reply-service/post/{id}")
    PostResponse getPostById(@PathVariable String id);
}
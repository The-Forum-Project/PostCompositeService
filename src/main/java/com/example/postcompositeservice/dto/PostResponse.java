package com.example.postcompositeservice.dto;

import com.example.postcompositeservice.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PostResponse {
    Post post;
}

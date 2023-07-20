package com.example.postcompositeservice.dto;

import com.example.postcompositeservice.domain.Post;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    Post post;
}

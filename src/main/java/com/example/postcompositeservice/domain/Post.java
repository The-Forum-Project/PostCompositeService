package com.example.postcompositeservice.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Document(collection = "posts")
public class Post {
    @Id
    private String postId;
    private Long userId;
    private String title;
    private String content;
    private Boolean isArchived;
    private String status;
    private Date dateCreated;
    private Date dateModified;
    private List<String> images;
    private List<String> attachments;
    private List<PostReply> postReplies;
}
package com.example.postcompositeservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SubReply {
    private Long userId;
    private String comment;
    private Boolean isActive;
    private Date dateCreated;
}
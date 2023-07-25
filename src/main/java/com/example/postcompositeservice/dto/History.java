package com.example.postcompositeservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class History {
    private Integer historyId;

    private Long userId;

    private String postId;

    private Date viewDate;
}

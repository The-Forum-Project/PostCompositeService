package com.example.postcompositeservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GeneralResponse {
    private String statusCode;
    private String message;
}

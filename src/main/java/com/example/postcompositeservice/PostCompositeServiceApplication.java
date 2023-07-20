package com.example.postcompositeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients({"com.example.postcompositeservice.service.remote"})
public class PostCompositeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostCompositeServiceApplication.class, args);
    }

}

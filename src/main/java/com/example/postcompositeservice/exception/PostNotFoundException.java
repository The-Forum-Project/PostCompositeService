package com.example.postcompositeservice.exception;

public class PostNotFoundException extends Exception{

    public PostNotFoundException() {
        super("Post is not found");
    }

}

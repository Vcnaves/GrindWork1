package com.project.grindwork.exceptions;

public class ObjectBadRequestException extends RuntimeException {

    public ObjectBadRequestException(String msg){
        super(msg);
    }

    public ObjectBadRequestException(String msg, Throwable cause){
        super(msg, cause);
    }
}

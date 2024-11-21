package com.project.grindwork.exceptions;

public class ObjectConflictException extends RuntimeException {

    public ObjectConflictException(String msg){
        super(msg);
    }

    public ObjectConflictException(String msg, Throwable cause){
        super(msg, cause);
    }
}

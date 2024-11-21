package com.project.grindwork.exceptions;

public class ObjectInternalErrorServiceException extends RuntimeException {
    
    public ObjectInternalErrorServiceException(String msg){
        super(msg);
    }

    public ObjectInternalErrorServiceException(String msg, Throwable cause){
        super(msg, cause);
    }
}

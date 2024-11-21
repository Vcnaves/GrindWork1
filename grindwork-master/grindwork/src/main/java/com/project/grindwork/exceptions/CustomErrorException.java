package com.project.grindwork.exceptions;

import com.project.grindwork.model.ErrorResponse;

public class CustomErrorException extends RuntimeException {
    private ErrorResponse errorResponse;

    public CustomErrorException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}

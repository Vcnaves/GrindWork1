package com.project.grindwork.model;

import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatusCode statusCode;
    private String message;
}

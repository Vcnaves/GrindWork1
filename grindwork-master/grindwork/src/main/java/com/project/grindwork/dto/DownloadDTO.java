package com.project.grindwork.dto;

import java.io.Serializable;

import org.springframework.core.io.Resource;

public class DownloadDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String contentType;
    private Resource resource;
    
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public Resource getResource() {
        return resource;
    }
    public void setResource(Resource resource) {
        this.resource = resource;
    }

}

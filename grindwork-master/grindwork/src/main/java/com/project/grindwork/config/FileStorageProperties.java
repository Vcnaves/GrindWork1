package com.project.grindwork.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    private String uploadDir;
    private String uploadDirAnun;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getUploadDirAnun() {
        return uploadDirAnun;
    }

    public void setUploadDirAnun(String uploadDirAnun) {
        this.uploadDirAnun = uploadDirAnun;
    }
    
}

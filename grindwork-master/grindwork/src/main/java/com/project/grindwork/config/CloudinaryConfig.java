package com.project.grindwork.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name}")
    private String CLOUD_NAME; // Remover o static

    @Value("${cloudinary.api_key}")
    private String API_KEY; // Remover o static

    @Value("${cloudinary.api_secret}")
    private String API_SECRET; // Remover o static

    @Bean
    public Cloudinary getCloudinary() { // Remover o static
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CLOUD_NAME,
                "api_key", API_KEY,
                "api_secret", API_SECRET));
    }
}

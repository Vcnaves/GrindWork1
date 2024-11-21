package com.project.grindwork.service;

import org.springframework.security.core.context.SecurityContextHolder;

import com.project.grindwork.model.Usuario;

public class UserService {
    
    public static Usuario authenticated(){
        try {
            return (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
}

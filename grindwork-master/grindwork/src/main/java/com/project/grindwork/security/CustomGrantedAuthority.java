package com.project.grindwork.security;

import org.springframework.security.core.GrantedAuthority;

public class CustomGrantedAuthority implements GrantedAuthority {
    
    private final String authority;

    public CustomGrantedAuthority(int perfil){
        String authorityValue = "ROLE_USER";
        switch (perfil) {
            case 1:
                authorityValue = "ROLE_ADMIN";
                break;
        }
        authority = authorityValue;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}

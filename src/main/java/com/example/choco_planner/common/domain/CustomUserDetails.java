package com.example.choco_planner.common.domain;

import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class CustomUserDetails extends UsernamePasswordAuthenticationToken {

    private final CustomUser customUser;

    public CustomUserDetails(Long id, String username, String name, Collection<? extends GrantedAuthority> authorities) {
        super(null, null, authorities);
        this.customUser = new CustomUser(id, username, name);
    }

}

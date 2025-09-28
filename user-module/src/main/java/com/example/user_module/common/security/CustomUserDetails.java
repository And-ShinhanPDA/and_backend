package com.example.user_module.common.security;

import com.example.user_module.auth.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record CustomUserDetails(UserEntity user) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 최소 권한: ROLE_USER 하나만 부여
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // DB에 password 가 있긴 하지만 JWT 인증에서는 안 쓸 수도 있음
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // email을 principal 로 쓰는 게 일반적
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

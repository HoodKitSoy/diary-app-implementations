package com.example.mydiaryapp.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.mydiaryapp.entity.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 簡易実装として空のリストを返す
        // 実際のアプリケーションでは、ユーザーの権限に応じて適切な権限を返す
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
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
    
    // ユーザーIDを取得するためのメソッド
    public String getUserId() {
        return user.getId();
    }
    
    // ユーザーオブジェクトを取得するためのメソッド
    public User getUser() {
        return user;
    }
}
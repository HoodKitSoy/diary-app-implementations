package com.example.mydiaryapp.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.mydiaryapp.entity.User;

import lombok.RequiredArgsConstructor;

/**
 * Spring SecurityのUserDetails実装クラスです。
 * 内部でUserエンティティをラップし、認証情報を提供します。
 */
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    /**
     * ユーザーの権限情報を返します。
     * @return GrantedAuthorityのコレクション（簡易実装では空のリスト）
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 簡易実装として空のリストを返す
        return new ArrayList<>();
    }

    /**
     * ユーザーのパスワードハッシュを返します。
     * @return パスワードハッシュ文字列
     */
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    /**
     * 認証時のユーザー名（ここではメールアドレス）を返します。
     * @return メールアドレス文字列
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /** @return アカウントの有効期限切れではない場合true */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** @return アカウントがロックされていない場合true */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** @return 資格情報の有効期限切れではない場合true */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** @return アカウントが有効である場合true */
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    /**
     * ユーザーIDを取得します。
     * @return 内部UserエンティティのID
     */
    public String getUserId() {
        return user.getId();
    }
    
    /**
     * 内部のUserエンティティを返します。
     * @return Userエンティティオブジェクト
     */
    public User getUser() {
        return user;
    }
}
package com.example.mydiaryapp.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.mydiaryapp.entity.User;
import com.example.mydiaryapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Spring Security用のユーザー情報サービス実装クラスです。
 * メールアドレスを基にデータベースからユーザーを検索し、
 * 認証情報としてCustomUserDetailsを提供します。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * メールアドレスをキーにユーザー情報をロードします。
     * @param email ユーザーのメールアドレス
     * @return 認証に使用されるUserDetailsオブジェクト(CustomUserDetails)
     * @throws UsernameNotFoundException ユーザーが存在しない場合にスローされます
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));
        
        return new CustomUserDetails(user);
    }
}
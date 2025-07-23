package com.example.mydiaryapp.service;

import com.example.mydiaryapp.dto.AuthRequest;
import com.example.mydiaryapp.dto.RegisterRequest;
import com.example.mydiaryapp.entity.User;
import com.example.mydiaryapp.repository.UserRepository;
import com.example.mydiaryapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 認証および登録に関するビジネスロジックを提供するサービスクラスです。
 * ユーザーの登録、ログイン処理を担当し、JWTトークンを生成します。
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * 新規ユーザー登録処理を実行します。
     * - メールアドレスとユーザー名の重複チェック
     * - パスワードのハッシュ化
     * - ユーザー情報の保存とJWTトークン生成
     * @param request ユーザー名、メールアドレス、パスワードを含む登録情報DTO
     * @return 登録結果としてユーザーID、ユーザー名、メールアドレス、JWTトークンを含むMap
     * @throws RuntimeException 登録時にメールアドレスまたはユーザー名が重複している場合
     */
    public Map<String, Object> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("このメールアドレスは既に使用されています");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("このユーザー名は既に使用されています");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", savedUser.getId());
        response.put("username", savedUser.getUsername());
        response.put("email", savedUser.getEmail());
        response.put("token", token);
        
        return response;
    }

    /**
     * ログイン認証を実行し、JWTトークンを発行します。
     * - 認証マネージャによる認証
     * - ユーザー情報の取得
     * - JWTトークン生成
     * @param request メールアドレスとパスワードを含む認証情報DTO
     * @return 認証結果としてユーザーID、ユーザー名、JWTトークンを含むMap
     * @throws RuntimeException 認証失敗やユーザーが見つからない場合
     */
    public Map<String, Object> login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        String token = jwtUtil.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("token", token);
        
        return response;
    }
}
package com.example.mydiaryapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mydiaryapp.dto.AuthRequest;
import com.example.mydiaryapp.dto.RegisterRequest;
import com.example.mydiaryapp.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 認証関連のエンドポイントを提供するコントローラークラスです。
 * ユーザーの登録、ログイン、ログアウト機能を実装しています。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {
    private final AuthService authService;

    /**
     * ユーザー登録を行います。
     * @param request ユーザー名、メールアドレス、パスワードを含む登録情報
     * @return 生成したユーザー情報とJWTトークンなどを含むMap
     *         成功時はHTTPステータス201(CREATED)、
     *         失敗時はHTTPステータス400(BAD_REQUEST)とエラーメッセージを返します。
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // 新規ユーザーを登録し、トークン等を生成
            Map<String, Object> response = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            // 登録失敗時のエラーハンドリング
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * ログイン認証を行い、JWTトークンを発行します。
     * @param request メールアドレスとパスワードを含む認証情報
     * @return 認証成功時はJWTトークン等を含むMapとHTTPステータス200(OK)、
     *         認証失敗時はHTTPステータス400(BAD_REQUEST)と固定エラーメッセージを返します。
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody AuthRequest request) {
        try {
            // 認証処理とトークン生成
            Map<String, Object> response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 認証失敗時は詳細を隠してメッセージ返却
            return ResponseEntity.badRequest().body(Map.of("error", "メールアドレスまたはパスワードが正しくありません"));
        }
    }

    /**
     * クライアント側でのトークン破棄を想定したログアウトを行います。
     * @return HTTPステータス204(NO_CONTENT)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // 現在サーバー側でセッションやトークンを管理していないため、空レスポンスで返却
        return ResponseEntity.noContent().build();
    }
}
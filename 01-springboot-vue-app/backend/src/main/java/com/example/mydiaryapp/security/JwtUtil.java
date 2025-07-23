package com.example.mydiaryapp.security;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * JWTトークンの生成・解析・検証を行うユーティリティクラスです。
 * シークレットキーと有効期限を設定し、トークンの発行と検証機能を提供します。
 */
@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 設定されたシークレットキーから署名用Keyを生成します。
     * @return HMAC-SHAキーオブジェクト
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * 指定されたメールアドレスをサブジェクトに含むJWTトークンを生成します。
     * @param email ユーザーのメールアドレス（トークンのサブジェクト）
     * @return 生成されたJWT文字列
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWTトークンからメールアドレス（サブジェクト）を抽出します。
     * @param token JWT文字列
     * @return トークンに含まれるサブジェクト（メールアドレス）
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * JWTトークンの有効期限日時を取得します。
     * @param token JWT文字列
     * @return トークンの有効期限日時
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 汎用的にJWTトークンから任意のクレーム情報を抽出します。
     * @param token JWT文字列
     * @param claimsResolver クレーム処理関数
     * @param <T> 戻り値の型
     * @return 抽出されたクレーム情報
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * JWTトークンをパースし、全てのクレーム情報を取得します。
     * @param token JWT文字列
     * @return クレームオブジェクト
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * トークンが期限切れかどうかを判定します。
     * @param token JWT文字列
     * @return 期限切れならtrue
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * トークンの有効性（メールアドレス一致かつ期限内）を検証します。
     * @param token JWT文字列
     * @param email 期待するサブジェクト（メールアドレス）
     * @return 検証結果（有効ならtrue）
     */
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
}
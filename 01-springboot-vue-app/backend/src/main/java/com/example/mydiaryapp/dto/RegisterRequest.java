package com.example.mydiaryapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ユーザー登録用のリクエストDTOクラスです。
 * ユーザー名、メールアドレス、パスワードを保持します。
 */
@Data
public class RegisterRequest {

    /**
     * 登録するユーザー名。
     * 必須で1～50文字の範囲です。
     */
    @NotBlank(message = "ユーザー名は必須です")
    @Size(min = 1, max = 50, message = "ユーザー名は1文字以上50文字以内で入力してください")
    private String username;

    /**
     * メールアドレス。
     * 必須かつ有効な形式である必要があります。
     */
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    /**
     * パスワード。
     * 必須で8文字以上かつ英数字を含む必要があります。
     */
    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", message = "パスワードは英数字を含む必要があります")
    private String password;
}
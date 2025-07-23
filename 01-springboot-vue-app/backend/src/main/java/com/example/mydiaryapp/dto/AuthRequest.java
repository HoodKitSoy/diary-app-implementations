package com.example.mydiaryapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ログイン用の認証リクエストDTOクラスです。
 * ユーザーのメールアドレスとパスワードを保持します。
 */
@Data
public class AuthRequest {

    /**
     * ユーザーのメールアドレス。
     * NotBlankかつEmail形式が要求されます。
     */
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    /**
     * ユーザーのパスワード。
     * NotBlankかつ8文字以上が要求されます。
     */
    @NotBlank(message = "パスワードは必須です")
    @Size(min = 8, message = "パスワードは8文字以上で入力してください")
    private String password;
}
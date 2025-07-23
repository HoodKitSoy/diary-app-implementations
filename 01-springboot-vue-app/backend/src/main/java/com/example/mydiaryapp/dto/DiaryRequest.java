package com.example.mydiaryapp.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 日記作成・更新用のリクエストDTOクラスです。
 * タイトル、本文、感情、タグ、画像リストを保持します。
 */
@Data
public class DiaryRequest {
    /**
     * 日記のタイトル。
     * 必須で、最大255文字です。
     */
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 255, message = "タイトルは255文字以内で入力してください")
    private String title;

    /**
     * 日記の本文。
     * オプションで、最大65,535文字まで許容します。
     */
    @Size(max = 65535, message = "本文は65,535文字以内で入力してください")
    private String content;

    /**
     * 日記の感情状態を表す文字列（例: "happy", "sad" など）。
     * オプションです。
     */
    private String emotion;

    /**
     * 日記に関連付けるタグ一覧。
     * オプションで複数の文字列を指定できます。
     */
    private List<String> tags;

    /**
     * 添付画像のリスト。複数の画像を含めることができます。
     */
    private List<ImageRequest> images;

    /**
     * 画像アップロード用の内部クラスです。
     * ファイル名とBase64エンコードされた画像データを保持します。
     */
    @Data
    public static class ImageRequest {
        /**
         * 画像のファイル名。
         * 必須です。
         */
        @NotBlank(message = "ファイル名は必須です")
        private String filename;

        /**
         * Base64エンコードされた画像データ。
         * 必須です。
         */
        @NotBlank(message = "画像データは必須です")
        private String data;
    }
}
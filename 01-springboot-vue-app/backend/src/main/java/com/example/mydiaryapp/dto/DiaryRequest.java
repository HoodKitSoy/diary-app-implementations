package com.example.mydiaryapp.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DiaryRequest {
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 255, message = "タイトルは255文字以内で入力してください")
    private String title;

    @Size(max = 65535, message = "本文は65,535文字以内で入力してください")
    private String content;

    private String emotion;
    private List<String> tags;
    private List<ImageRequest> images;

    @Data
    public static class ImageRequest {
        @NotBlank(message = "ファイル名は必須です")
        private String filename;

        @NotBlank(message = "画像データは必須です")
        private String data;
    }
}
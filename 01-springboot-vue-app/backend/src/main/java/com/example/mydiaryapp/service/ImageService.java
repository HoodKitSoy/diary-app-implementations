package com.example.mydiaryapp.service;

import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.mydiaryapp.dto.DiaryRequest;
import com.example.mydiaryapp.entity.Diary;
import com.example.mydiaryapp.entity.Image;

import lombok.RequiredArgsConstructor;

/**
 * 画像データの保存・処理を行うサービスクラスです。
 * Base64形式の画像データを解析し、一意のファイル名を生成して
 * ストレージに保存します。
 */
@Service
@RequiredArgsConstructor
public class ImageService {

    /**
     * Diaryエンティティに紐付く画像を保存します。
     * Base64データからMIMEタイプと拡張子を抽出し、
     * 一意のファイル名を生成してストレージに保存後、
     * Imageエンティティを生成・返却します。
     * @param diary 紐付けるDiaryエンティティ
     * @param imageRequest 画像アップロード情報(DiaryRequest.ImageRequest)
     * @return 保存済みのImageエンティティ（id未保存の場合も含む）
     * @throws RuntimeException 保存処理に失敗した場合
     */
    public Image saveImage(Diary diary, DiaryRequest.ImageRequest imageRequest) {
        // Base64データからファイル拡張子を取得
        String data = imageRequest.getData();
        String mimeType = extractMimeType(data);
        String extension = getExtensionFromMimeType(mimeType);
        
        // 一意なファイル名を生成
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // 実際の実装では、ここでファイルをクラウドストレージやローカルディスクに保存
        // 今回は簡易実装として、Base64データをそのままURLとして保存
        String imageUrl = saveImageToStorage(data, uniqueFilename);
        
        Image image = new Image();
        image.setDiary(diary);
        image.setFilename(imageRequest.getFilename());
        image.setUrl(imageUrl);
        
        return image;
    }

    /**
     * Data URL形式の文字列からMIMEタイプを抽出します。
     * @param dataUrl Base64を含むデータURL文字列
     * @return MIMEタイプ文字列（例: image/jpeg）
     */
    private String extractMimeType(String dataUrl) {
        if (dataUrl.startsWith("data:")) {
            int commaIndex = dataUrl.indexOf(',');
            if (commaIndex > 0) {
                String header = dataUrl.substring(0, commaIndex);
                // data:image/jpeg;base64 から image/jpeg を抽出
                int colonIndex = header.indexOf(':');
                int semicolonIndex = header.indexOf(';');
                if (colonIndex > 0 && semicolonIndex > colonIndex) {
                    return header.substring(colonIndex + 1, semicolonIndex);
                }
            }
        }
        return "image/jpeg"; // デフォルト
    }

    /**
     * MIMEタイプに対応するファイル拡張子を返します。
     * @param mimeType MIMEタイプ文字列
     * @return ファイル拡張子 (例: .jpg)
     */
    private String getExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            case "image/webp":
                return ".webp";
            default:
                return ".jpg";
        }
    }

    /**
     * 画像のBase64データをストレージに保存し、アクセス用URLを生成します。
     * @param dataUrl Base64を含むデータURL文字列
     * @param filename 保存用の一意なファイル名
     * @return 画像の公開URL
     * @throws RuntimeException 無効なデータまたは保存失敗時
     */
    private String saveImageToStorage(String dataUrl, String filename) {
        try {
            // data:image/jpeg;base64,<base64-data> から base64データ部分を抽出
            int commaIndex = dataUrl.indexOf(',');
            if (commaIndex > 0) {
                String base64Data = dataUrl.substring(commaIndex + 1);
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                
                // 実際の実装では、ここでファイルシステムやクラウドストレージに保存
                // 例: AWS S3, Google Cloud Storage, ローカルディスクなど
                
                // 簡易実装として、静的ファイルのURLを返す
                // 実際にはファイル保存処理を実装する必要があります
                return "http://localhost:8080/images/" + filename;
            }
        } catch (Exception e) {
            throw new RuntimeException("画像の保存に失敗しました: " + e.getMessage());
        }
        
        throw new RuntimeException("無効な画像データです");
    }
}
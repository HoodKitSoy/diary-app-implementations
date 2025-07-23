package com.example.mydiaryapp.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 画像エンティティクラス。
 * 日記に紐付く画像情報を表します。
 */
@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    /**
     * 画像の一意なUUID形式ID。
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    /**
     * 紐付く日記エンティティ。
     * 多対一のリレーションで、必須項目です。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;

    /**
     * 画像の保存先URL。
     */
    @Column(length = 2048, nullable = false)
    private String url;

    /**
     * オリジナルのファイル名。
     */
    @Column(nullable = false)
    private String filename;

    /**
     * レコード作成日時。
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 新規レコード挿入時に呼び出されるプリパース処理。
     * createdAt に現在日時を設定します。
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
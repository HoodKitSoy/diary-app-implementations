package com.example.mydiaryapp.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 日記エンティティクラス。
 * ユーザーが作成する日記の基本情報を表します。
 */
@Entity
@Table(name = "diaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    /**
     * 日記の一意なUUID形式ID。
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    /**
     * 日記の所有者ユーザー。
     * 多対一のリレーションで、必須項目です。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 日記のタイトル。
     */
    @Column(nullable = false)
    private String title;

    /**
     * 日記の本文。
     * TEXT型カラムとしてマッピングされます。
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 感情状態（例: happy, sad等）。
     */
    @Column(length = 20)
    private String emotion;

    /**
     * レコード作成日時。
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * レコード更新日時。
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 関連付けられた画像リスト。
     * 一対多のリレーションで、オーファン除去を使用します。
     */
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    /**
     * 関連付けられたタグの集合。
     * 多対多のリレーションで、中間テーブル diary_tags を使用します。
     */
    @ManyToMany
    @JoinTable(
        name = "diary_tags",
        joinColumns = @JoinColumn(name = "diary_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    /**
     * 新規レコード挿入時に呼び出されるプリパース処理。
     * createdAt と updatedAt に現在日時を設定します。
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * 更新時に呼び出されるプリパース処理。
     * updatedAt に現在日時を設定します。
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
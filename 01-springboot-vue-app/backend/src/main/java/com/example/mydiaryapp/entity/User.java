package com.example.mydiaryapp.entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザーエンティティクラス。
 * アプリケーションの使用者情報を表します。
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * ユーザーの一意なUUID形式ID。
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    /**
     * ユーザー名。
     * 最大50文字、一意制約があります。
     */
    @Column(length = 50, nullable = false, unique = true)
    private String username;

    /**
     * メールアドレス。
     * 最大255文字、一意制約があり、有効な形式が要求されます。
     */
    @Column(length = 255, nullable = false, unique = true)
    private String email;

    /**
     * パスワードのハッシュ値。
     * セキュリティ確保のため平文は保存しません。
     */
    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    /**
     * テーマ設定（"light" または "dark" など）。
     * 初期値は "light"。
     */
    @Column(length = 10, nullable = false)
    private String theme = "light";

    /**
     * リマインダー通知時刻。
     * オプションで設定できます。
     */
    @Column(name = "reminder_time")
    private LocalTime reminderTime;

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
     * このユーザーに属する日記リスト。
     * 一対多のリレーションでオーファン除去を使用します。
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Diary> diaries;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
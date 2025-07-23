package com.example.mydiaryapp.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * タグエンティティクラス。
 * 日記に付与できるラベル情報を表します。
 */
@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    /**
     * タグの一意なID（自動生成）。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * タグ名。
     * 最大50文字、一意制約があります。
     */
    @Column(length = 50, nullable = false, unique = true)
    private String name;

    /**
     * このタグが関連付けられた日記の集合。
     * 多対多のリレーションを通じて管理されます。
     */
    @ManyToMany(mappedBy = "tags")
    private Set<Diary> diaries;
}
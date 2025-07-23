package com.example.mydiaryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.mydiaryapp.entity.Tag;

/**
 * タグデータに対する永続化操作を行うリポジトリインターフェースです。
 * JpaRepositoryを継承し、タグ取得やユーザー関連タグの検索メソッドを提供します。
 */
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    /**
     * タグ名でタグを取得します。
     * @param name タグ名
     * @return 指定名のタグを Optional でラップして返します
     */
    Optional<Tag> findByName(String name);

    /**
     * 指定ユーザーが使用しているタグの一覧を取得します。
     * @param userId ユーザーID
     * @return ユーザーが関連付けたタグのリスト（重複なし）
     */
    @Query("SELECT DISTINCT t FROM Tag t JOIN t.diaries d WHERE d.user.id = :userId")
    List<Tag> findByUserId(@Param("userId") String userId);
}
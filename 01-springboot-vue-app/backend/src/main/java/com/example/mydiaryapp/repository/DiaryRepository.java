package com.example.mydiaryapp.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.mydiaryapp.entity.Diary;

/**
 * 日記データに対する永続化操作を行うリポジトリインターフェースです。
 * JpaRepositoryを継承し、CRUDおよびカスタム検索メソッドを提供します。
 */
@Repository
public interface DiaryRepository extends JpaRepository<Diary, String> {

    /**
     * 指定ユーザーの全日記を作成日時降順でページング取得します。
     * @param userId ユーザーID
     * @param pageable ページ情報
     * @return ページングされた日記リスト
     */
    Page<Diary> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * タイトルまたは本文にキーワードを含む日記を作成日時降順でページング取得します。
     * @param userId ユーザーID
     * @param keyword 検索キーワード
     * @param pageable ページ情報
     * @return ページングされた日記リスト
     */
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND (d.title LIKE %:keyword% OR d.content LIKE %:keyword%) ORDER BY d.createdAt DESC")
    Page<Diary> findByUserIdAndKeyword(@Param("userId") String userId, 
                                       @Param("keyword") String keyword, 
                                       Pageable pageable);

    /**
     * 特定のタグ名でフィルタリングした日記を作成日時降順でページング取得します。
     * @param userId ユーザーID
     * @param tagName タグ名
     * @param pageable ページ情報
     * @return ページングされた日記リスト
     */
    @Query("SELECT d FROM Diary d JOIN d.tags t WHERE d.user.id = :userId AND t.name = :tagName ORDER BY d.createdAt DESC")
    Page<Diary> findByUserIdAndTag(@Param("userId") String userId, 
                                   @Param("tagName") String tagName, 
                                   Pageable pageable);

    /**
     * 指定月（開始日時～終了日時）に作成された日記をページング取得します。
     * @param userId ユーザーID
     * @param startDate 開始日時（inclusive）
     * @param endDate 終了日時（exclusive）
     * @param pageable ページ情報
     * @return ページングされた日記リスト
     */
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND d.createdAt >= :startDate AND d.createdAt < :endDate ORDER BY d.createdAt DESC")
    Page<Diary> findByUserIdAndMonth(@Param("userId") String userId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    /**
     * 指定IDかつ指定ユーザーに紐づく日記を取得します。
     * @param id 日記ID
     * @param userId ユーザーID
     * @return Optionalでラップされた日記エンティティ
     */
    Optional<Diary> findByIdAndUserId(String id, String userId);
}
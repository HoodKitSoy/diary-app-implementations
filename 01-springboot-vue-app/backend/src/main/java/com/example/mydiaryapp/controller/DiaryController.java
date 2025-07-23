package com.example.mydiaryapp.controller;

import java.util.Map;

import org.springframework.http.HttpStatus; // この行を追加
import org.springframework.http.ResponseEntity; // この行を追加
import org.springframework.security.core.annotation.AuthenticationPrincipal; // この行を追加
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mydiaryapp.dto.DiaryRequest;
import com.example.mydiaryapp.entity.User;
import com.example.mydiaryapp.repository.UserRepository;
import com.example.mydiaryapp.security.CustomUserDetails;
import com.example.mydiaryapp.service.DiaryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 日記エントリの取得、作成、更新、削除を提供するコントローラークラスです。
 * 認証済みユーザーの日記操作を担当します。
 */
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DiaryController {
    private final DiaryService diaryService;
    private final UserRepository userRepository;

    /**
     * ページネーション、検索クエリ、タグ、月フィルタを用いて日記リストを取得します。
     * @param userDetails 認証済みのユーザー情報
     * @param q 検索キーワード（オプション）
     * @param tag 絞り込みタグ（オプション）
     * @param month 絞り込み月（YYYY-MM形式、オプション）
     * @param page ページ番号（デフォルト1）
     * @param limit 1ページあたりの件数（デフォルト10）
     * @return 日記リスト、総件数、ページ情報などを含むMapとHTTPステータス200(OK)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDiaries(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.getDiaries(userId, q, tag, month, page, limit);
        return ResponseEntity.ok(response);
    }

    /**
     * 新規日記を作成します。
     * @param userDetails 認証済みのユーザー情報
     * @param request タイトル、本文、タグなどの日記情報
     * @return 作成した日記の詳細を含むMapとHTTPステータス201(CREATED)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DiaryRequest request) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.createDiary(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 指定IDの日記を取得します。
     * @param userDetails 認証済みのユーザー情報
     * @param diaryId 日記ID
     * @return 日記の詳細を含むMapとHTTPステータス200(OK)
     */
    @GetMapping("/{diaryId}")
    public ResponseEntity<Map<String, Object>> getDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String diaryId) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.getDiary(userId, diaryId);
        return ResponseEntity.ok(response);
    }

    /**
     * 指定IDの日記を更新します。
     * @param userDetails 認証済みのユーザー情報
     * @param diaryId 更新対象の日記ID
     * @param request 更新後のタイトル、本文、タグなどを含む日記情報
     * @return 更新した日記の詳細を含むMapとHTTPステータス200(OK)
     */
    @PutMapping("/{diaryId}")
    public ResponseEntity<Map<String, Object>> updateDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String diaryId,
            @Valid @RequestBody DiaryRequest request) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.updateDiary(userId, diaryId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 指定IDの日記を削除します。
     * @param userDetails 認証済みのユーザー情報
     * @param diaryId 削除対象の日記ID
     * @return HTTPステータス204(NO_CONTENT)
     */
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String diaryId) {
        
        String userId = getUserId(userDetails);
        diaryService.deleteDiary(userId, diaryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * UserDetailsからユーザーIDを取得します。
     * CustomUserDetailsの場合は直接取得し、Emailベースの場合はDB検索を行います。
     * @param userDetails 認証済みのユーザー情報
     * @return ユーザーID
     */
    /**
     * UserDetailsからユーザーIDを取得します。
     * CustomUserDetailsの場合は直接取得し、Emailベースの場合はDB検索を行います。
     * @param userDetails 認証済みのユーザー情報
     * @return ユーザーID
     */
    private String getUserId(UserDetails userDetails) {
        if (userDetails instanceof CustomUserDetails) {
            return ((CustomUserDetails) userDetails).getUserId();
        }
        // フォールバック: メールアドレスからユーザーを検索
        return userRepository.findByEmail(userDetails.getUsername())
            .map(User::getId)
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));
    }
}
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

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class DiaryController {
    private final DiaryService diaryService;
    private final UserRepository userRepository; // この行を追加

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

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DiaryRequest request) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.createDiary(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<Map<String, Object>> getDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String diaryId) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.getDiary(userId, diaryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{diaryId}")
    public ResponseEntity<Map<String, Object>> updateDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String diaryId,
            @Valid @RequestBody DiaryRequest request) {
        
        String userId = getUserId(userDetails);
        Map<String, Object> response = diaryService.updateDiary(userId, diaryId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String diaryId) {
        
        String userId = getUserId(userDetails);
        diaryService.deleteDiary(userId, diaryId);
        return ResponseEntity.noContent().build();
    }

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
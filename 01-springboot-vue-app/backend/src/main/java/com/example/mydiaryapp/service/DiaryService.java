package com.example.mydiaryapp.service;

import com.example.mydiaryapp.dto.DiaryRequest;
import com.example.mydiaryapp.entity.Diary;
import com.example.mydiaryapp.entity.Image;
import com.example.mydiaryapp.entity.Tag;
import com.example.mydiaryapp.entity.User;
import com.example.mydiaryapp.repository.DiaryRepository;
import com.example.mydiaryapp.repository.TagRepository;
import com.example.mydiaryapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日記に関わるビジネスロジックを提供するサービスクラスです。
 * 日記の一覧取得、作成、詳細取得、更新、削除処理を担当します。
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ImageService imageService;

    /**
     * 指定ユーザーの日記一覧を取得します。
     * キーワード検索、タグフィルタ、月フィルタおよびページネーションをサポートします。
     * @param userId ユーザーID
     * @param keyword 検索キーワード(タイトル・本文, 任意)
     * @param tagName フィルタ用タグ名(任意)
     * @param month フィルタ用年月(YYYY-MM形式, 任意)
     * @param page ページ番号(1始まり)
     * @param limit 1ページあたりの件数
     * @return 日記リストとページ情報を含むMap
     */
    public Map<String, Object> getDiaries(String userId, String keyword, String tagName, 
                                          String month, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Diary> diaryPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            diaryPage = diaryRepository.findByUserIdAndKeyword(userId, keyword, pageable);
        } else if (tagName != null && !tagName.trim().isEmpty()) {
            diaryPage = diaryRepository.findByUserIdAndTag(userId, tagName, pageable);
        } else if (month != null && !month.trim().isEmpty()) {
            YearMonth yearMonth = YearMonth.parse(month);
            LocalDateTime startDate = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime endDate = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            diaryPage = diaryRepository.findByUserIdAndMonth(userId, startDate, endDate, pageable);
        } else {
            diaryPage = diaryRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        }

        List<Map<String, Object>> diaries = diaryPage.getContent().stream()
            .map(this::convertToMap)
            .collect(Collectors.toList());

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("total", diaryPage.getTotalElements());
        pagination.put("page", page);
        pagination.put("limit", limit);
        pagination.put("totalPages", diaryPage.getTotalPages());

        Map<String, Object> response = new HashMap<>();
        response.put("diaries", diaries);
        response.put("pagination", pagination);

        return response;
    }

    /**
     * 新規日記を作成します。
     * タグの作成・関連付けおよび画像の保存処理を行います。
     * @param userId ユーザーID
     * @param request 日記情報(DiaryRequest)
     * @return 作成した日記の詳細を含むMap
     */
    public Map<String, Object> createDiary(String userId, DiaryRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        Diary diary = new Diary();
        diary.setUser(user);
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setEmotion(request.getEmotion());

        // タグの処理
        if (request.getTags() != null) {
            Set<Tag> tags = request.getTags().stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
            diary.setTags(tags);
        }

        Diary savedDiary = diaryRepository.save(diary);

        // 画像の処理
        if (request.getImages() != null) {
            List<Image> images = request.getImages().stream()
                .map(imageReq -> imageService.saveImage(savedDiary, imageReq))
                .collect(Collectors.toList());
            savedDiary.setImages(images);
        }

        return convertToDetailMap(savedDiary);
    }

    /**
     * 指定IDの日記詳細を取得します。
     * @param userId ユーザーID
     * @param diaryId 日記ID
     * @return 日記詳細を含むMap
     */
    public Map<String, Object> getDiary(String userId, String diaryId) {
        Diary diary = diaryRepository.findByIdAndUserId(diaryId, userId)
            .orElseThrow(() -> new RuntimeException("日記が見つかりません"));
        return convertToDetailMap(diary);
    }

    /**
     * 指定IDの日記を更新します。
     * タグと画像の更新処理を行い、変更を保存します。
     * @param userId ユーザーID
     * @param diaryId 日記ID
     * @param request 更新用日記情報(DiaryRequest)
     * @return 更新後の日記詳細を含むMap
     */
    public Map<String, Object> updateDiary(String userId, String diaryId, DiaryRequest request) {
        Diary diary = diaryRepository.findByIdAndUserId(diaryId, userId)
            .orElseThrow(() -> new RuntimeException("日記が見つかりません"));

        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());
        diary.setEmotion(request.getEmotion());

        // タグの更新
        if (request.getTags() != null) {
            Set<Tag> tags = request.getTags().stream()
                .map(this::findOrCreateTag)
                .collect(Collectors.toSet());
            diary.setTags(tags);
        }

        // 画像の更新（既存の画像を削除して新しい画像を追加）
        if (request.getImages() != null) {
            diary.getImages().clear();
            List<Image> images = request.getImages().stream()
                .map(imageReq -> imageService.saveImage(diary, imageReq))
                .collect(Collectors.toList());
            diary.setImages(images);
        }

        Diary savedDiary = diaryRepository.save(diary);
        return convertToDetailMap(savedDiary);
    }

    /**
     * 指定IDの日記を削除します。
     * @param userId ユーザーID
     * @param diaryId 日記ID
     */
    public void deleteDiary(String userId, String diaryId) {
        Diary diary = diaryRepository.findByIdAndUserId(diaryId, userId)
            .orElseThrow(() -> new RuntimeException("日記が見つかりません"));
        diaryRepository.delete(diary);
    }

    private Tag findOrCreateTag(String tagName) {
        return tagRepository.findByName(tagName)
            .orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(tagName);
                return tagRepository.save(newTag);
            });
    }

    private Map<String, Object> convertToMap(Diary diary) {
        Map<String, Object> map = new HashMap<>();
        map.put("diaryId", diary.getId());
        map.put("title", diary.getTitle());
        map.put("createdAt", diary.getCreatedAt().toString());
        map.put("emotion", diary.getEmotion());
        map.put("tags", diary.getTags().stream()
            .map(Tag::getName)
            .collect(Collectors.toList()));
        return map;
    }

    private Map<String, Object> convertToDetailMap(Diary diary) {
        Map<String, Object> map = new HashMap<>();
        map.put("diaryId", diary.getId());
        map.put("title", diary.getTitle());
        map.put("content", diary.getContent());
        map.put("emotion", diary.getEmotion());
        map.put("createdAt", diary.getCreatedAt().toString());
        map.put("updatedAt", diary.getUpdatedAt().toString());
        map.put("tags", diary.getTags().stream()
            .map(Tag::getName)
            .collect(Collectors.toList()));
        map.put("images", diary.getImages().stream()
            .map(image -> {
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("imageId", image.getId());
                imageMap.put("url", image.getUrl());
                return imageMap;
            })
            .collect(Collectors.toList()));
        return map;
    }
}
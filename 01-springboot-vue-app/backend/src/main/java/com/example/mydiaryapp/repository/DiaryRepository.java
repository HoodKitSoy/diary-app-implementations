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

@Repository
public interface DiaryRepository extends JpaRepository<Diary, String> {
    Page<Diary> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND " +
           "(d.title LIKE %:keyword% OR d.content LIKE %:keyword%) " +
           "ORDER BY d.createdAt DESC")
    Page<Diary> findByUserIdAndKeyword(@Param("userId") String userId, 
                                       @Param("keyword") String keyword, 
                                       Pageable pageable);
    
    @Query("SELECT d FROM Diary d JOIN d.tags t WHERE d.user.id = :userId AND t.name = :tagName " +
           "ORDER BY d.createdAt DESC")
    Page<Diary> findByUserIdAndTag(@Param("userId") String userId, 
                                   @Param("tagName") String tagName, 
                                   Pageable pageable);
    
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND " +
           "d.createdAt >= :startDate AND d.createdAt < :endDate " +
           "ORDER BY d.createdAt DESC")
    Page<Diary> findByUserIdAndMonth(@Param("userId") String userId,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);
    
    Optional<Diary> findByIdAndUserId(String id, String userId);
}
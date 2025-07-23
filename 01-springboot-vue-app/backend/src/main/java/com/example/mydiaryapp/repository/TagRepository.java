package com.example.mydiaryapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.mydiaryapp.entity.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    
    @Query("SELECT DISTINCT t FROM Tag t JOIN t.diaries d WHERE d.user.id = :userId")
    List<Tag> findByUserId(@Param("userId") String userId);
}
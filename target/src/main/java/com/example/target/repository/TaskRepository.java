package com.example.target.repository;

import com.example.target.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND " +
            "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> searchTasksByKeywords(Long userId, String keyword);

    List<Task> findByUserIdAndDueTimeBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
      AND (
            :keyword IS NULL OR :keyword = '' OR
            LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
      AND (
            :startTime IS NULL OR t.dueTime >= :startTime
          )
      AND (
            :endTime IS NULL OR t.dueTime < :endTime
          )
""")
    List<Task> searchTasks(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
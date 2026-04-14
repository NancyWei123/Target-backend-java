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



    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND (
        LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    """)
        List<Task> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND t.dueTime >= :startDateTime
    """)
        List<Task> searchByStartDate(@Param("userId") Long userId, @Param("startDateTime") LocalDateTime startDateTime);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND t.dueTime < :endDateTime
    """)
        List<Task> searchByEndDate(@Param("userId") Long userId, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND t.dueTime >= :startDateTime
    AND t.dueTime < :endDateTime
    """)
        List<Task> searchByDateRange(@Param("userId") Long userId,
                                     @Param("startDateTime") LocalDateTime startDateTime,
                                     @Param("endDateTime") LocalDateTime endDateTime);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND (
        LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND t.dueTime >= :startDateTime
    """)
        List<Task> searchByKeywordAndStartDate(@Param("userId") Long userId,
                                               @Param("keyword") String keyword,
                                               @Param("startDateTime") LocalDateTime startDateTime);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND (
        LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND t.dueTime < :endDateTime
    """)
        List<Task> searchByKeywordAndEndDate(@Param("userId") Long userId,
                                             @Param("keyword") String keyword,
                                             @Param("endDateTime") LocalDateTime endDateTime);

    @Query("""
    SELECT t FROM Task t
    WHERE t.user.id = :userId
    AND (
        LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    AND t.dueTime >= :startDateTime
    AND t.dueTime < :endDateTime
    """)
        List<Task> searchByKeywordAndDateRange(@Param("userId") Long userId,
                                               @Param("keyword") String keyword,
                                               @Param("startDateTime") LocalDateTime startDateTime,
                                               @Param("endDateTime") LocalDateTime endDateTime);
}
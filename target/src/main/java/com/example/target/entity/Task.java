package com.example.target.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data               // getter + setter + toString + equals + hashCode
@NoArgsConstructor  // no-args constructor
@AllArgsConstructor // all-args constructor
@Builder            // builder pattern
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String priority; // LOW, MEDIUM, HIGH
    private LocalDateTime dueTime;
    private Boolean completed = false;
}

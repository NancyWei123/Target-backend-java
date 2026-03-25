package com.example.target.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private String priority;
    private LocalDateTime dueTime;
    private Boolean completed;
}
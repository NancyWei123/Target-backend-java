package com.example.target.mapper;

import com.example.target.dto.TaskDTO;
import com.example.target.entity.Task;

public class TaskMapper {

    // ✅ Entity → DTO
    public static TaskDTO toDTO(Task task) {
        if (task == null) return null;

        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority())
                .dueTime(task.getDueTime())
                .completed(task.getCompleted())
                .userId(task.getUser() != null ? task.getUser().getId() : null)
                .build();
    }

    // ✅ DTO → Entity
    public static Task toEntity(TaskDTO dto) {
        if (dto == null) return null;

        return Task.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .dueTime(dto.getDueTime())
                .completed(dto.getCompleted())
                .build();
    }
}
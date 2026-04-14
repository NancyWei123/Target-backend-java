package com.example.target.service;

import com.example.target.dto.TaskDTO;
import com.example.target.entity.Task;
import com.example.target.entity.User;
import com.example.target.mapper.TaskMapper;
import com.example.target.repository.TaskRepository;
import com.example.target.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    // ✅ Constructor injection (better than @Autowired)
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }
    // ✅ Get tasks for a specific user
    public List<TaskDTO> getTasksByUserId(Long userId) {
        return taskRepository.findByUserId(userId)
                .stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    // ✅ GET all tasks
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(TaskMapper::toDTO)
                .toList();
    }

    // ✅ GET task by id
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        return TaskMapper.toDTO(task);
    }

    // ✅ CREATE task
    public TaskDTO createTask(TaskDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Task task = TaskMapper.toEntity(dto);
        task.setUser(user);   // set user_id here

        Task saved = taskRepository.save(task);
        return TaskMapper.toDTO(saved);
    }

    // ✅ UPDATE task
    public TaskDTO updateTask(Long id, Task updatedTask, Long userId) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (existingTask.getUser() == null || !existingTask.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this task");
        }

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setDueTime(updatedTask.getDueTime());
        existingTask.setCompleted(updatedTask.getCompleted());

        Task saved = taskRepository.save(existingTask);
        return TaskMapper.toDTO(saved);
    }

    // ✅ DELETE task
    public void deleteTask(Long id, Long userId) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (existingTask.getUser() == null || !existingTask.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to delete this task");
        }

        taskRepository.delete(existingTask);
    }

    public List<TaskDTO> searchTasks(Long userId, String keyword, LocalDate startDate, LocalDate endDate) {
        String safeKeyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        LocalDateTime startDateTime = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        List<Task> tasks;

        if (safeKeyword == null && startDateTime == null && endDateTime == null) {
            tasks = taskRepository.findByUserId(userId);
        } else if (safeKeyword != null && startDateTime == null && endDateTime == null) {
            tasks = taskRepository.searchByKeyword(userId, safeKeyword);
        } else if (safeKeyword == null && startDateTime != null && endDateTime == null) {
            tasks = taskRepository.searchByStartDate(userId, startDateTime);
        } else if (safeKeyword == null && startDateTime == null && endDateTime != null) {
            tasks = taskRepository.searchByEndDate(userId, endDateTime);
        } else if (safeKeyword == null) {
            tasks = taskRepository.searchByDateRange(userId, startDateTime, endDateTime);
        } else if (startDateTime == null && endDateTime == null) {
            tasks = taskRepository.searchByKeyword(userId, safeKeyword);
        } else if (endDateTime == null) {
            tasks = taskRepository.searchByKeywordAndStartDate(userId, safeKeyword, startDateTime);
        } else if (startDateTime == null) {
            tasks = taskRepository.searchByKeywordAndEndDate(userId, safeKeyword, endDateTime);
        } else {
            tasks = taskRepository.searchByKeywordAndDateRange(userId, safeKeyword, startDateTime, endDateTime);
        }

        return tasks.stream().map(TaskMapper::toDTO).toList();
    }

    public TaskDTO finishTask(Long taskId, Long userId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (existingTask.getUser() == null || !existingTask.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this task");
        }
        existingTask.setCompleted(true);
        existingTask.setCompletedTime(LocalDateTime.now());
        Task saved = taskRepository.save(existingTask);
        return TaskMapper.toDTO(saved);
    }

    public TaskDTO undoTask(Long taskId, Long userId) {
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (existingTask.getUser() == null || !existingTask.getUser().getId().equals(userId)) {
            throw new RuntimeException("You are not allowed to update this task");
        }
        existingTask.setCompleted(false);
        existingTask.setCompletedTime(null);
        Task saved = taskRepository.save(existingTask);
        return TaskMapper.toDTO(saved);
    }
}
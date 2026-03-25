package com.example.target.service;

import com.example.target.dto.TaskDTO;
import com.example.target.entity.Task;
import com.example.target.mapper.TaskMapper;
import com.example.target.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // ✅ Constructor injection (better than @Autowired)
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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
    public TaskDTO createTask(Task dto) {
        Task task = TaskMapper.toEntity(dto);
        Task saved = taskRepository.save(task);
        return TaskMapper.toDTO(saved);
    }

    // ✅ UPDATE task
    public TaskDTO updateTask(Long id, Task dto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        // update fields
        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setPriority(dto.getPriority());
        existing.setDueTime(dto.getDueTime());
        existing.setCompleted(dto.getCompleted());

        Task updated = taskRepository.save(existing);
        return TaskMapper.toDTO(updated);
    }

    // ✅ DELETE task
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
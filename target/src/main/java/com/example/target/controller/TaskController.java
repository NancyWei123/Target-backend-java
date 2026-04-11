package com.example.target.controller;

import com.example.target.dto.TaskDTO;
import com.example.target.entity.Task;
import com.example.target.entity.User;
import com.example.target.service.TaskService;
import com.example.target.tools.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<TaskDTO> getMyTasks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return taskService.getTasksByUserId(userId);
    }

    // ✅ GET task by id
    @GetMapping("/{id}")
    public TaskDTO getTaskById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, Errors errors) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        TaskDTO res=taskService.getTaskById(id);
        if (res.getUserId() == null || !res.getUserId().equals(userId)) {
            throw new RuntimeException("You are not allowed to access this task");
        }
        return res;
    }

    // ✅ CREATE new task
    @PostMapping
    public TaskDTO createTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody TaskDTO dto
    ) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);

        return taskService.createTask(dto, userId);
    }

    // ✅ UPDATE task
    @PutMapping("/{id}")
    public TaskDTO updateTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody Task task
    ) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return taskService.updateTask(id, task, userId);
    }
    // ✅ FINISH task
    @PutMapping("/finish/{taskId}")
    public TaskDTO changeStatusTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long taskId
    ) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return taskService.finishTask(taskId, userId);
    }

    @PutMapping("/undo/{taskId}")
    public TaskDTO undoTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long taskId
    ) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return taskService.undoTask(taskId, userId);
    }

    // ✅ DELETE task
    @DeleteMapping("/{id}")
    public void deleteTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id
    ) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);

        taskService.deleteTask(id, userId);
    }
    @GetMapping("/search/keyword")
    public List<TaskDTO> searchTasksByKeyword(@RequestHeader("Authorization") String authHeader,@RequestParam String keyword) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return taskService.searchTasksByKeyword(userId,keyword);
    }
    @GetMapping("/search/deadline")
    public List<TaskDTO> searchTasksByDDL(@RequestHeader("Authorization") String authHeader,@RequestParam LocalDate startDate,@RequestParam LocalDate endDate) {
        String token = authHeader.substring(7);
        Long userId = JwtUtil.extractUserId(token);
        return taskService.searchTasksByDDL(userId,startDate,endDate);
    }
}

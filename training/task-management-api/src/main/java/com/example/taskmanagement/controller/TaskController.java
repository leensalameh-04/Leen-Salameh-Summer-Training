package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskRequestDTO;
import com.example.taskmanagement.dto.TaskResponseDTO;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // POST /api/tasks
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @RequestBody TaskRequestDTO requestDTO) {

        Task task = TaskMapper.toEntity(requestDTO);
        Task savedTask = taskService.createTask(task, requestDTO.getProjectId());

        return new ResponseEntity<>(
                TaskMapper.toResponse(savedTask),
                HttpStatus.CREATED
        );
    }

    // GET /api/tasks
    @GetMapping
    public List<TaskResponseDTO> getAllTasks() {
        return taskService.getAllTasks()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id) {

        Task task = taskService.getTaskById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                TaskMapper.toResponse(task)
        );
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @RequestBody TaskRequestDTO requestDTO) {

        Task task = TaskMapper.toEntity(requestDTO);
        Task updatedTask = taskService.updateTask(id, task, requestDTO.getProjectId());

        return ResponseEntity.ok(
                TaskMapper.toResponse(updatedTask)
        );
    }

    // PATCH /api/tasks/{id}/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {

        Task updatedTask = taskService.updateStatus(id, status);

        return ResponseEntity.ok(
                TaskMapper.toResponse(updatedTask)
        );
    }

    // GET /api/tasks/status/{status}
    @GetMapping("/status/{status}")
    public List<TaskResponseDTO> getTasksByStatus(
            @PathVariable TaskStatus status) {

        return taskService.getTasksByStatus(status)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // GET /api/tasks/priority/{priority}
    @GetMapping("/priority/{priority}")
    public List<TaskResponseDTO> getTasksByPriority(
            @PathVariable TaskPriority priority) {

        return taskService.getTasksByPriority(priority)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // GET /api/tasks/overdue
    @GetMapping("/overdue")
    public List<TaskResponseDTO> getOverdueTasks() {
        return taskService.getOverdueTasks()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
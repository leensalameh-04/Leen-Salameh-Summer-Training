package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskRequestDTO;
import com.example.taskmanagement.dto.TaskResponseDTO;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // POST /api/tasks (201 CREATED)
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(
            @Valid @RequestBody TaskRequestDTO requestDTO) {

        Task task = TaskMapper.toEntity(requestDTO);
        Task savedTask = taskService.createTask(task, requestDTO.getProjectId());

        return new ResponseEntity<>(
                TaskMapper.toResponse(savedTask),
                HttpStatus.CREATED
        );
    }

    // GET /api/tasks with Filtering, Pagination & Sorting (200 OK)
    @GetMapping
    public Page<TaskResponseDTO> getTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Boolean overdue,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        return taskService.getTasks(status, priority, projectId, overdue, pageable)
                .map(TaskMapper::toResponse);
    }

    // GET /api/tasks/{id} (200 OK)
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(
            @PathVariable Long id) {

        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(TaskMapper.toResponse(task));
    }

    // PUT /api/tasks/{id} (200 OK)
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO requestDTO) {

        Task task = TaskMapper.toEntity(requestDTO);
        Task updatedTask = taskService.updateTask(id, task, requestDTO.getProjectId());

        return ResponseEntity.ok(TaskMapper.toResponse(updatedTask));
    }

    // PATCH /api/tasks/{id}/status (200 OK)
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {

        Task updatedTask = taskService.updateStatus(id, status);

        return ResponseEntity.ok(TaskMapper.toResponse(updatedTask));
    }

    // GET /api/tasks/status/{status} (200 OK)
    @GetMapping("/status/{status}")
    public List<TaskResponseDTO> getTasksByStatus(
            @PathVariable TaskStatus status) {

        return taskService.getTasksByStatus(status)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // GET /api/tasks/priority/{priority} (200 OK)
    @GetMapping("/priority/{priority}")
    public List<TaskResponseDTO> getTasksByPriority(
            @PathVariable TaskPriority priority) {

        return taskService.getTasksByPriority(priority)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // GET /api/tasks/overdue (200 OK)
    @GetMapping("/overdue")
    public List<TaskResponseDTO> getOverdueTasks() {
        return taskService.getOverdueTasks()
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }

    // DELETE /api/tasks/{id} (204 NO CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id) {

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
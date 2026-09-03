package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.ProjectRequestDTO;
import com.example.taskmanagement.dto.ProjectResponseDTO;
import com.example.taskmanagement.dto.TaskResponseDTO;
import com.example.taskmanagement.mapper.ProjectMapper;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.service.ProjectService;
import com.example.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    // POST /api/projects (201 CREATED)
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO requestDTO) {
        Project project = ProjectMapper.toEntity(requestDTO);
        Project saved = projectService.createProject(project);
        return new ResponseEntity<>(ProjectMapper.toResponse(saved), HttpStatus.CREATED);
    }

    // GET /api/projects (200 OK)
    @GetMapping
    public List<ProjectResponseDTO> getAllProjects() {
        return projectService.getAllProjects()
                .stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    // GET /api/projects/{id} (200 OK)
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(ProjectMapper.toResponse(project));
    }

    // PUT /api/projects/{id} (200 OK)
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequestDTO requestDTO) {
        Project project = ProjectMapper.toEntity(requestDTO);
        Project updated = projectService.updateProject(id, project);
        return ResponseEntity.ok(ProjectMapper.toResponse(updated));
    }

    // DELETE /api/projects/{id} (204 NO CONTENT)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/projects/{projectId}/tasks (200 OK)
    @GetMapping("/{projectId}/tasks")
    public List<TaskResponseDTO> getTasksByProjectId(@PathVariable Long projectId) {
        return taskService.getTasksByProjectId(projectId)
                .stream()
                .map(TaskMapper::toResponse)
                .toList();
    }
}

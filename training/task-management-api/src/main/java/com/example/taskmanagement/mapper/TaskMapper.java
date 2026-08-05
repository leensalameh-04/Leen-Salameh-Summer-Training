package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.TaskRequestDTO;
import com.example.taskmanagement.dto.TaskResponseDTO;
import com.example.taskmanagement.model.Task;

public class TaskMapper {

    public static Task toEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        return task;
    }

    public static TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
        }
        return dto;
    }
}
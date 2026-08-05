package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.ProjectRequestDTO;
import com.example.taskmanagement.dto.ProjectResponseDTO;
import com.example.taskmanagement.model.Project;

public class ProjectMapper {

    public static Project toEntity(ProjectRequestDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        return project;
    }

    public static ProjectResponseDTO toResponse(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setCreatedDate(project.getCreatedDate());
        return dto;
    }
}

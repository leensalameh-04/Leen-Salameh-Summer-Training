package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Project;
import java.util.List;

public interface ProjectService {
    List<Project> getAllProjects();
    Project getProjectById(Long id);
    Project createProject(Project project);
    Project updateProject(Long id, Project project);
    void deleteProject(Long id);
}

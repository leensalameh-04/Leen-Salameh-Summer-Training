package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    public Project createProject(Project project) {
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        project.setCreatedDate(LocalDate.now());
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(Long id, Project project) {
        Project existing = getProjectById(id);
        if (existing == null) {
            throw new RuntimeException("Project not found");
        }
        if (project.getName() == null || project.getName().isBlank()) {
            throw new IllegalArgumentException("Project name is required");
        }
        existing.setName(project.getName());
        existing.setDescription(project.getDescription());
        return projectRepository.save(existing);
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }
}

package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.DuplicateProjectNameException;
import com.example.taskmanagement.exception.ProjectNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        logger.info("Fetching all projects");
        return projectRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(Long id) {
        logger.info("Fetching project with ID: {}", id);
        return projectRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Project lookup failed: Project not found with ID: {}", id);
                    return new ProjectNotFoundException(id);
                });
    }

    @Override
    public Project createProject(Project project) {
        logger.info("Creating project with name: '{}'", project.getName());

        if (project.getName() == null || project.getName().isBlank()) {
            logger.warn("Project creation failed: Project name is required");
            throw new IllegalArgumentException("Project name is required");
        }

        if (projectRepository.existsByName(project.getName())) {
            logger.warn("Project creation failed: Duplicate project name '{}'", project.getName());
            throw new DuplicateProjectNameException(project.getName());
        }

        project.setCreatedDate(LocalDate.now());
        Project savedProject = projectRepository.save(project);
        logger.info("Project created successfully with ID: {}", savedProject.getId());
        return savedProject;
    }

    @Override
    public Project updateProject(Long id, Project project) {
        logger.info("Updating project with ID: {}", id);
        Project existing = getProjectById(id);

        if (project.getName() == null || project.getName().isBlank()) {
            logger.warn("Project update failed for ID {}: Project name is required", id);
            throw new IllegalArgumentException("Project name is required");
        }

        if (projectRepository.existsByNameAndIdNot(project.getName(), id)) {
            logger.warn("Project update failed for ID {}: Duplicate project name '{}'", id, project.getName());
            throw new DuplicateProjectNameException(project.getName());
        }

        existing.setName(project.getName());
        existing.setDescription(project.getDescription());

        Project updatedProject = projectRepository.save(existing);
        logger.info("Project with ID {} updated successfully", id);
        return updatedProject;
    }

    @Override
    public void deleteProject(Long id) {
        logger.info("Deleting project with ID: {}", id);
        if (!projectRepository.existsById(id)) {
            logger.warn("Project deletion failed: Project not found with ID: {}", id);
            throw new ProjectNotFoundException(id);
        }
        projectRepository.deleteById(id);
        logger.info("Project with ID {} deleted successfully", id);
    }
}

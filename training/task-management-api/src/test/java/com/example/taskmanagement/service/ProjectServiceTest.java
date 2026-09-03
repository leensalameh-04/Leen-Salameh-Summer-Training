package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.DuplicateProjectNameException;
import com.example.taskmanagement.exception.ProjectNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project sampleProject;

    @BeforeEach
    void setUp() {
        sampleProject = new Project(1L, "Unique Project", "Description", LocalDate.now());
    }

    @Test
    void testCreateProjectSuccess() {
        when(projectRepository.existsByName("Unique Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(i -> i.getArgument(0));

        Project created = projectService.createProject(sampleProject);

        assertThat(created.getName()).isEqualTo("Unique Project");
    }

    @Test
    void testCreateProjectDuplicateNameThrowsException() {
        when(projectRepository.existsByName("Unique Project")).thenReturn(true);

        assertThatThrownBy(() -> projectService.createProject(sampleProject))
                .isInstanceOf(DuplicateProjectNameException.class)
                .hasMessage("A project with the name 'Unique Project' already exists");
    }

    @Test
    void testGetProjectByIdNotFoundThrowsException() {
        when(projectRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.getProjectById(99L))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessage("Project not found with id: 99");
    }
}

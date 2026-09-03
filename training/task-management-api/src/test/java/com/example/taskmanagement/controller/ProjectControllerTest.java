package com.example.taskmanagement.controller;

import com.example.taskmanagement.exception.DuplicateProjectNameException;
import com.example.taskmanagement.exception.GlobalExceptionHandler;
import com.example.taskmanagement.exception.ProjectNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.service.ProjectService;
import com.example.taskmanagement.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private ProjectController projectController;

    private Project sampleProject;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleProject = new Project(1L, "Spring Boot Project", "Test Description", LocalDate.now());
    }

    @Test
    void createProject_withValidInput_shouldReturn201Created() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "name": "Spring Boot Project",
                    "description": "Test Description"
                }
                """;

        when(projectService.createProject(any(Project.class))).thenReturn(sampleProject);

        // Act & Assert
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Spring Boot Project"));
    }

    @Test
    void createProject_withDuplicateName_shouldReturn409Conflict() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "name": "Spring Boot Project"
                }
                """;

        when(projectService.createProject(any(Project.class)))
                .thenThrow(new DuplicateProjectNameException("Spring Boot Project"));

        // Act & Assert
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("A project with the name 'Spring Boot Project' already exists"));
    }

    @Test
    void getProjectById_whenDoesNotExist_shouldReturn404NotFound() throws Exception {
        // Arrange
        when(projectService.getProjectById(99L)).thenThrow(new ProjectNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/api/projects/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Project not found with id: 99"));
    }

    @Test
    void deleteProject_whenExists_shouldReturn204NoContent() throws Exception {
        // Arrange
        doNothing().when(projectService).deleteProject(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());
    }
}

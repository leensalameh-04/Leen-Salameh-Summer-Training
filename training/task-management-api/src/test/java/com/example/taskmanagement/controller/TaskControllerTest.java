package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskRequestDTO;
import com.example.taskmanagement.exception.GlobalExceptionHandler;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("MockMvc Test Task");
        sampleTask.setDescription("Test Description");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setPriority(TaskPriority.HIGH);
        sampleTask.setDueDate(LocalDate.now().plusDays(5));
    }

    @Test
    void createTask_withValidInput_shouldReturn201Created() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "title": "MockMvc Test Task",
                    "description": "Test Description",
                    "priority": "HIGH"
                }
                """;

        when(taskService.createTask(any(Task.class), eq(null))).thenReturn(sampleTask);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("MockMvc Test Task"))
                .andExpect(jsonPath("$.status").value("TODO"));
    }

    @Test
    void createTask_withBlankTitle_shouldReturn400BadRequest() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "title": "",
                    "description": "Test Description"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_whenTaskExists_shouldReturn200Ok() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(sampleTask);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("MockMvc Test Task"));
    }

    @Test
    void getTaskById_whenTaskDoesNotExist_shouldReturn404NotFound() throws Exception {
        // Arrange
        when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));

        // Act & Assert
        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
    }

    @Test
    void deleteTask_whenTaskExists_shouldReturn204NoContent() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_whenTaskDoesNotExist_shouldReturn404NotFound() throws Exception {
        // Arrange
        doThrow(new TaskNotFoundException(99L)).when(taskService).deleteTask(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}

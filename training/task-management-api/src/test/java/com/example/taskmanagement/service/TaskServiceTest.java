package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.InvalidTaskStatusException;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.repository.ProjectRepository;
import com.example.taskmanagement.repository.TaskRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task sampleTask;
    private Project sampleProject;

    @BeforeEach
    void setUp() {
        sampleProject = new Project(1L, "Test Project", "Desc", LocalDate.now());

        sampleTask = new Task();
        sampleTask.setId(10L);
        sampleTask.setTitle("Sample Task");
        sampleTask.setStatus(TaskStatus.TODO);
        sampleTask.setPriority(TaskPriority.MEDIUM);
        sampleTask.setDueDate(LocalDate.now().plusDays(1));
    }

    @Test
    void testCreateTaskSuccess() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(sampleProject));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task created = taskService.createTask(sampleTask, 1L);

        assertThat(created.getTitle()).isEqualTo("Sample Task");
        assertThat(created.getStatus()).isEqualTo(TaskStatus.TODO);
        assertThat(created.getProject()).isEqualTo(sampleProject);
        verify(taskRepository).save(sampleTask);
    }

    @Test
    void testCreateTaskMissingTitleThrowsException() {
        sampleTask.setTitle("");

        assertThatThrownBy(() -> taskService.createTask(sampleTask, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title is required");
    }

    @Test
    void testCreateTaskPastDueDateThrowsException() {
        sampleTask.setDueDate(LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> taskService.createTask(sampleTask, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Due date must be in the present or future");
    }

    @Test
    void testUpdateStatusTransitionFromTodoToDoneThrowsException() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));

        assertThatThrownBy(() -> taskService.updateStatus(10L, TaskStatus.DONE))
                .isInstanceOf(InvalidTaskStatusException.class)
                .hasMessage("Task must be IN_PROGRESS before it can be marked as DONE");
    }

    @Test
    void testUpdateStatusTransitionFromTodoToInProgressSuccess() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updated = taskService.updateStatus(10L, TaskStatus.IN_PROGRESS);

        assertThat(updated.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
    }

    @Test
    void testGetTaskByIdNotFoundThrowsTaskNotFoundException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessage("Task not found with id: 99");
    }
}

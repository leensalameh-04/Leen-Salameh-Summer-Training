package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.repository.ProjectRepository;
import com.example.taskmanagement.repository.TaskRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public Task createTask(Task task, Long projectId) {
        // Rule 1: Title is required
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        // Rule 2: Default status is TODO
        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        // Rule 3: Due date must be in the present or future
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be in the present or future");
        }

        // Associate with Project if projectId is provided
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
            task.setProject(project);
        }

        return taskRepository.save(task);
    }

    @Override
    public Task updateTask(Long id, Task task, Long projectId) {
        Task existingTask = getTaskById(id);
        if (existingTask == null) {
            throw new RuntimeException("Task not found");
        }

        // Rule 1: Title is required for updates as well
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        // Rule 3: Due date must be in the present or future
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date must be in the present or future");
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());

        // Associate/update Project relationship
        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found with id: " + projectId));
            existingTask.setProject(project);
        } else {
            existingTask.setProject(null);
        }

        return taskRepository.save(existingTask);
    }

    @Override
    public Task updateStatus(Long id, TaskStatus status) {
        Task task = getTaskById(id);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }

        TaskStatus currentStatus = task.getStatus();

        // Rule: Completed tasks cannot be reopened or changed
        if (currentStatus == TaskStatus.DONE && status != TaskStatus.DONE) {
            throw new IllegalStateException("Cannot change status of a completed task");
        }

        // Rule: Task must be IN_PROGRESS before it can be marked as DONE
        if (currentStatus == TaskStatus.TODO && status == TaskStatus.DONE) {
            throw new IllegalStateException("Task must be IN_PROGRESS before it can be marked as DONE");
        }

        task.setStatus(status);
        return taskRepository.save(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
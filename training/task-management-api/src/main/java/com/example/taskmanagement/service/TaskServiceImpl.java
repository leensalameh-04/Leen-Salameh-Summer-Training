package com.example.taskmanagement.service;

import com.example.taskmanagement.exception.InvalidTaskStatusException;
import com.example.taskmanagement.exception.ProjectNotFoundException;
import com.example.taskmanagement.exception.TaskNotFoundException;
import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.repository.ProjectRepository;
import com.example.taskmanagement.repository.TaskRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        logger.info("Fetching all tasks from repository");
        return taskRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> getTasks(TaskStatus status, TaskPriority priority, Long projectId, Boolean overdue, Pageable pageable) {
        logger.info("Fetching paged tasks with filters - status: {}, priority: {}, projectId: {}, overdue: {}, pageable: {}",
                status, priority, projectId, overdue, pageable);

        Specification<Task> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (projectId != null) {
                predicates.add(cb.equal(root.get("project").get("id"), projectId));
            }
            if (Boolean.TRUE.equals(overdue)) {
                predicates.add(cb.lessThan(root.get("dueDate"), LocalDate.now()));
                predicates.add(cb.notEqual(root.get("status"), TaskStatus.DONE));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return taskRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Task getTaskById(Long id) {
        logger.info("Fetching task with ID: {}", id);
        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task lookup failed: Task not found with ID: {}", id);
                    return new TaskNotFoundException(id);
                });
    }

    @Override
    public Task createTask(Task task, Long projectId) {
        logger.info("Creating task with title: '{}', projectId: {}", task.getTitle(), projectId);

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            logger.warn("Task creation failed: Title is required");
            throw new IllegalArgumentException("Title is required");
        }

        if (task.getStatus() == null) {
            task.setStatus(TaskStatus.TODO);
        }

        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            logger.warn("Task creation failed: Due date {} is in the past", task.getDueDate());
            throw new IllegalArgumentException("Due date must be in the present or future");
        }

        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        logger.warn("Task creation failed: Project not found with ID: {}", projectId);
                        return new ProjectNotFoundException(projectId);
                    });
            task.setProject(project);
        }

        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return savedTask;
    }

    @Override
    public Task updateTask(Long id, Task task, Long projectId) {
        logger.info("Updating task with ID: {}", id);
        Task existingTask = getTaskById(id);

        if (task.getTitle() == null || task.getTitle().isBlank()) {
            logger.warn("Task update failed for ID {}: Title is required", id);
            throw new IllegalArgumentException("Title is required");
        }

        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDate.now())) {
            logger.warn("Task update failed for ID {}: Due date {} is in the past", id, task.getDueDate());
            throw new IllegalArgumentException("Due date must be in the present or future");
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());

        if (projectId != null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        logger.warn("Task update failed for ID {}: Project not found with ID: {}", id, projectId);
                        return new ProjectNotFoundException(projectId);
                    });
            existingTask.setProject(project);
        } else {
            existingTask.setProject(null);
        }

        Task updatedTask = taskRepository.save(existingTask);
        logger.info("Task with ID {} updated successfully", id);
        return updatedTask;
    }

    @Override
    public Task updateStatus(Long id, TaskStatus status) {
        logger.info("Updating status for task ID {} to {}", id, status);
        Task task = getTaskById(id);

        TaskStatus currentStatus = task.getStatus();

        if (currentStatus == TaskStatus.DONE && status != TaskStatus.DONE) {
            logger.warn("Invalid status transition for task ID {}: Cannot change completed task from DONE to {}", id, status);
            throw new InvalidTaskStatusException("Cannot change status of a completed task");
        }

        if (currentStatus == TaskStatus.TODO && status == TaskStatus.DONE) {
            logger.warn("Invalid status transition for task ID {}: Cannot transition directly from TODO to DONE", id);
            throw new InvalidTaskStatusException("Task must be IN_PROGRESS before it can be marked as DONE");
        }

        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        logger.info("Status for task ID {} updated to {}", id, status);
        return updatedTask;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByStatus(TaskStatus status) {
        logger.info("Fetching tasks with status: {}", status);
        return taskRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByPriority(TaskPriority priority) {
        logger.info("Fetching tasks with priority: {}", priority);
        return taskRepository.findByPriority(priority);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getTasksByProjectId(Long projectId) {
        logger.info("Fetching tasks for projectId: {}", projectId);
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> getOverdueTasks() {
        logger.info("Fetching overdue tasks");
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE);
    }

    @Override
    public void deleteTask(Long id) {
        logger.info("Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            logger.warn("Task deletion failed: Task not found with ID: {}", id);
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        logger.info("Task with ID {} deleted successfully", id);
    }
}
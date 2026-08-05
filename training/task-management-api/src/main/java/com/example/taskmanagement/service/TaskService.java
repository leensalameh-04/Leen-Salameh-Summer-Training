package com.example.taskmanagement.service;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;

import java.util.List;

public interface TaskService {

    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task createTask(Task task, Long projectId);

    Task updateTask(Long id, Task task, Long projectId);

    Task updateStatus(Long id, TaskStatus status);

    List<Task> getTasksByStatus(TaskStatus status);

    List<Task> getTasksByPriority(TaskPriority priority);

    List<Task> getTasksByProjectId(Long projectId);

    List<Task> getOverdueTasks();

    void deleteTask(Long id);
}
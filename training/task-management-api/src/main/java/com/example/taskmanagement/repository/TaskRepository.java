package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(TaskPriority priority);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);
}

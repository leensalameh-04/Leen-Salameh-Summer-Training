package com.example.taskmanagement.controller;

import com.example.taskmanagement.model.Task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TaskController {

    private List<Task> tasks = new ArrayList<>();

    @GetMapping("/api/tasks")
    public List<Task> getAllTasks() {
        return tasks;
    }

    @GetMapping("/api/tasks/{id}")
    public Task getTaskById(@PathVariable Long id) {
        for (Task task : tasks) {
            if (task.getId().equals(id)) {
                return task;
            }
        }
        return null;
    }

    @PostMapping("/api/tasks")
    public Task createTask(@RequestBody Task task) {
        tasks.add(task);
        return task;
    }
   @DeleteMapping("/api/tasks/{id}")
public void deleteTask(@PathVariable Long id) {
    System.out.println("DELETE METHOD WORKS");
    tasks.removeIf(task -> task.getId().equals(id));
}
}
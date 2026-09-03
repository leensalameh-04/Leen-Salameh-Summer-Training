package com.example.taskmanagement.repository;

import com.example.taskmanagement.model.Project;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskPriority;
import com.example.taskmanagement.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;

    @BeforeEach
    void setUp() {
        testProject = new Project();
        testProject.setName("Spring Boot Day 3");
        testProject.setDescription("JPA Integration Project");
        testProject.setCreatedDate(LocalDate.now());
        testProject = projectRepository.save(testProject);
    }

    @Test
    void testSaveAndFindById() {
        Task task = new Task();
        task.setTitle("Learn JPA");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        task.setProject(testProject);

        Task savedTask = taskRepository.save(task);

        assertThat(savedTask.getId()).isNotNull();
        assertThat(taskRepository.findById(savedTask.getId())).isPresent();
    }

    @Test
    void testFindByStatus() {
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setStatus(TaskStatus.TODO);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);

        taskRepository.save(task1);
        taskRepository.save(task2);

        List<Task> todoTasks = taskRepository.findByStatus(TaskStatus.TODO);
        assertThat(todoTasks).hasSize(1);
        assertThat(todoTasks.get(0).getTitle()).isEqualTo("Task 1");
    }

    @Test
    void testFindByPriority() {
        Task task = new Task();
        task.setTitle("High Priority Task");
        task.setStatus(TaskStatus.TODO);
        task.setPriority(TaskPriority.HIGH);
        taskRepository.save(task);

        List<Task> highPriorityTasks = taskRepository.findByPriority(TaskPriority.HIGH);
        assertThat(highPriorityTasks).hasSize(1);
        assertThat(highPriorityTasks.get(0).getTitle()).isEqualTo("High Priority Task");
    }

    @Test
    void testFindByProjectId() {
        Task task = new Task();
        task.setTitle("Project Task");
        task.setStatus(TaskStatus.TODO);
        task.setProject(testProject);
        taskRepository.save(task);

        List<Task> projectTasks = taskRepository.findByProjectId(testProject.getId());
        assertThat(projectTasks).hasSize(1);
        assertThat(projectTasks.get(0).getProject().getId()).isEqualTo(testProject.getId());
    }

    @Test
    void testFindByDueDateBeforeAndStatusNot() {
        Task overdueTask = new Task();
        overdueTask.setTitle("Overdue Task");
        overdueTask.setStatus(TaskStatus.IN_PROGRESS);
        overdueTask.setDueDate(LocalDate.now().minusDays(2));
        taskRepository.save(overdueTask);

        Task completedOverdueTask = new Task();
        completedOverdueTask.setTitle("Completed Overdue Task");
        completedOverdueTask.setStatus(TaskStatus.DONE);
        completedOverdueTask.setDueDate(LocalDate.now().minusDays(2));
        taskRepository.save(completedOverdueTask);

        List<Task> overdueList = taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE);
        assertThat(overdueList).hasSize(1);
        assertThat(overdueList.get(0).getTitle()).isEqualTo("Overdue Task");
    }
}

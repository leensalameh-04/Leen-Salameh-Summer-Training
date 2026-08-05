package com.example.taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;

public class ProjectRequestDTO {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    public ProjectRequestDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

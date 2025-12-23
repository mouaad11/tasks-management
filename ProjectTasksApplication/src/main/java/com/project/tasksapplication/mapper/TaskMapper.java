package com.project.tasksapplication.mapper;

import com.project.tasksapplication.dto.request.TaskRequest;
import com.project.tasksapplication.dto.response.TaskResponse;
import com.project.tasksapplication.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .completed(false)
                .build();
    }

    public TaskResponse toDto(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .projectId(task.getProject().getId())
                .build();
    }
}
package com.project.tasksapplication.mapper;


import com.project.tasksapplication.dto.request.ProjectRequest;
import com.project.tasksapplication.dto.response.ProjectResponse;
import com.project.tasksapplication.model.Project;
import com.project.tasksapplication.model.Task;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectMapper {

    public Project toEntity(ProjectRequest request) {
        return Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();
    }

    public ProjectResponse toDto(Project project) {
        List<Task> tasks = project.getTasks();

        if (tasks == null) {
            tasks = new ArrayList<>();
        }

        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(Task::isCompleted).count();

        double progressPercentage = 0.0;
        if (totalTasks > 0) {
            progressPercentage = ((double) completedTasks / totalTasks) * 100;
        }

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                .build();
    }
}
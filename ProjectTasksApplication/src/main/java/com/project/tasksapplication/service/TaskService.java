package com.project.tasksapplication.service;

import com.project.tasksapplication.dto.request.TaskRequest;
import com.project.tasksapplication.dto.response.PageResponse;
import com.project.tasksapplication.dto.response.TaskResponse;
import com.project.tasksapplication.mapper.TaskMapper;
import com.project.tasksapplication.model.Project;
import com.project.tasksapplication.model.Task;
import com.project.tasksapplication.model.User;
import com.project.tasksapplication.repository.ProjectRepository;
import com.project.tasksapplication.repository.TaskRepository;
import com.project.tasksapplication.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request) {
        User user = getCurrentUser();

        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found");
        }

        Project project = projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new RuntimeException("Access denied: You can only add tasks to projects you own"));

        Task task = taskMapper.toEntity(request);
        task.setProject(project);

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        User user = getCurrentUser();

        if (!projectRepository.existsById(projectId)) { 
            throw new EntityNotFoundException("Project not found");
        }

        projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Access denied"));

        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getTasksByProjectIdPaginated(Long projectId, int page, int size, String search, Boolean completed) {
        User user = getCurrentUser();

        if (!projectRepository.existsById(projectId)) { 
            throw new EntityNotFoundException("Project not found");
        }

        projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Access denied"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByProjectIdWithFilters(
                projectId,
                search != null && !search.trim().isEmpty() ? search.trim() : null,
                completed,
                pageable
        );

        List<TaskResponse> content = taskPage.getContent().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<TaskResponse>builder()
                .content(content)
                .page(taskPage.getNumber())
                .size(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .first(taskPage.isFirst())
                .last(taskPage.isLast())
                .build();
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User user = getCurrentUser();
        if (!task.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, boolean completed) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User user = getCurrentUser();
        if (!task.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        task.setCompleted(completed);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User user = getCurrentUser();
        if (!task.getProject().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        taskRepository.delete(task);
    }
}
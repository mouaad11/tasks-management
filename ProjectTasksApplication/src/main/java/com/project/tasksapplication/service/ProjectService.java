package com.project.tasksapplication.service;


import com.project.tasksapplication.dto.request.ProjectRequest;
import com.project.tasksapplication.dto.response.PageResponse;
import com.project.tasksapplication.dto.response.ProjectResponse;
import com.project.tasksapplication.mapper.ProjectMapper;
import com.project.tasksapplication.model.Project;
import com.project.tasksapplication.model.User;
import com.project.tasksapplication.repository.ProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        User user = getCurrentUser();

        Project project = projectMapper.toEntity(request);
        project.setUser(user);

        Project savedProject = projectRepository.save(project);
        return projectMapper.toDto(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllUserProjects() {
        User user = getCurrentUser();
        List<Project> projects = projectRepository.findByUserId(user.getId());

        return projects.stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> getAllUserProjectsPaginated(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projectPage = projectRepository.findByUserId(user.getId(), pageable);

        List<ProjectResponse> content = projectPage.getContent().stream()
                .map(projectMapper::toDto)
                .collect(Collectors.toList());

        return PageResponse.<ProjectResponse>builder()
                .content(content)
                .page(projectPage.getNumber())
                .size(projectPage.getSize())
                .totalElements(projectPage.getTotalElements())
                .totalPages(projectPage.getTotalPages())
                .first(projectPage.isFirst())
                .last(projectPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long projectId) {
        User user = getCurrentUser();

        Project project = projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied"));

        return projectMapper.toDto(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request) {
        User user = getCurrentUser();

        Project project = projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied"));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Project updatedProject = projectRepository.save(project);
        return projectMapper.toDto(updatedProject);
    }

    @Transactional
    public void deleteProject(Long projectId) {
        User user = getCurrentUser();

        Project project = projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Project not found or access denied"));

        projectRepository.delete(project);
    }
}
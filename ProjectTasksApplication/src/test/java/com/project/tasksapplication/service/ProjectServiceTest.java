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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private ProjectService projectService;

    private User testUser;
    private Project testProject;
    private ProjectRequest projectRequest;
    private ProjectResponse projectResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .password("password")
                .createdAt(LocalDateTime.now())
                .build();

        testProject = Project.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .user(testUser)
                .createdAt(LocalDateTime.now())
                .build();

        projectRequest = new ProjectRequest();
        projectRequest.setTitle("Test Project");
        projectRequest.setDescription("Test Description");

        projectResponse = ProjectResponse.builder()
                .id(1L)
                .title("Test Project")
                .description("Test Description")
                .createdAt(LocalDateTime.now())
                .totalTasks(0)
                .completedTasks(0)
                .progressPercentage(0.0)
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void testCreateProject_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectMapper.toEntity(projectRequest)).thenReturn(testProject);
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectMapper.toDto(testProject)).thenReturn(projectResponse);

        // Act
        ProjectResponse result = projectService.createProject(projectRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Project", result.getTitle());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectMapper, times(1)).toDto(testProject);
    }

    @Test
    void testCreateProject_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            projectService.createProject(projectRequest);
        });
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testGetAllUserProjects_Success() {
        // Arrange
        List<Project> projects = Arrays.asList(testProject);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserId(1L)).thenReturn(projects);
        when(projectMapper.toDto(testProject)).thenReturn(projectResponse);

        // Act
        List<ProjectResponse> result = projectService.getAllUserProjects();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findByUserId(1L);
    }

    @Test
    void testGetAllUserProjectsPaginated_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Project> projectPage = new PageImpl<>(Arrays.asList(testProject), pageable, 1);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(projectPage);
        when(projectMapper.toDto(testProject)).thenReturn(projectResponse);

        // Act
        PageResponse<ProjectResponse> result = projectService.getAllUserProjectsPaginated(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
    }

    @Test
    void testGetProjectById_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        when(projectMapper.toDto(testProject)).thenReturn(projectResponse);

        // Act
        ProjectResponse result = projectService.getProjectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectRepository, times(1)).findByIdAndUserId(1L, 1L);
    }

    @Test
    void testGetProjectById_NotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.getProjectById(1L);
        });
    }

    @Test
    void testUpdateProject_Success() {
        // Arrange
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setTitle("Updated Title");
        updateRequest.setDescription("Updated Description");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        ProjectResponse updatedResponse = ProjectResponse.builder()
                .id(1L)
                .title("Updated Title")
                .description("Updated Description")
                .createdAt(LocalDateTime.now())
                .totalTasks(0)
                .completedTasks(0)
                .progressPercentage(0.0)
                .build();
        when(projectMapper.toDto(testProject)).thenReturn(updatedResponse);

        // Act
        ProjectResponse result = projectService.updateProject(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testUpdateProject_NotFound() {
        // Arrange
        ProjectRequest updateRequest = new ProjectRequest();
        updateRequest.setTitle("Updated Title");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.updateProject(1L, updateRequest);
        });
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void testDeleteProject_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        doNothing().when(projectRepository).delete(testProject);

        // Act
        projectService.deleteProject(1L);

        // Assert
        verify(projectRepository, times(1)).delete(testProject);
    }

    @Test
    void testDeleteProject_NotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.deleteProject(1L);
        });
        verify(projectRepository, never()).delete(any(Project.class));
    }
}


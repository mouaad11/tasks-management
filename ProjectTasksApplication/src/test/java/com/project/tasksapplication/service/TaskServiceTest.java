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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Project testProject;
    private Task testTask;
    private TaskRequest taskRequest;
    private TaskResponse taskResponse;

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

        testTask = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .project(testProject)
                .createdAt(LocalDateTime.now())
                .build();

        taskRequest = new TaskRequest();
        taskRequest.setTitle("Test Task");
        taskRequest.setDescription("Test Description");
        taskRequest.setDueDate(LocalDateTime.now().plusDays(1));

        taskResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .projectId(1L)
                .createdAt(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(1))
                .build();

        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getName()).thenReturn("test@example.com");
    }

    @Test
    void testCreateTask_Success() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        when(taskMapper.toEntity(taskRequest)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toDto(testTask)).thenReturn(taskResponse);

        // Act
        TaskResponse result = taskService.createTask(1L, taskRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(taskMapper, times(1)).toDto(testTask);
    }

    @Test
    void testCreateTask_ProjectNotFound() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.createTask(1L, taskRequest);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testCreateTask_AccessDenied() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.createTask(1L, taskRequest);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testGetTasksByProjectId_Success() {
        // Arrange
        List<Task> tasks = Arrays.asList(testTask);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProjectId(1L)).thenReturn(tasks);
        when(taskMapper.toDto(testTask)).thenReturn(taskResponse);

        // Act
        List<TaskResponse> result = taskService.getTasksByProjectId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByProjectId(1L);
    }

    @Test
    void testGetTasksByProjectIdPaginated_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask), pageable, 1);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProjectIdWithFilters(eq(1L), eq(null), eq(null), any(Pageable.class)))
                .thenReturn(taskPage);
        when(taskMapper.toDto(testTask)).thenReturn(taskResponse);

        // Act
        PageResponse<TaskResponse> result = taskService.getTasksByProjectIdPaginated(1L, 0, 10, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
    }

    @Test
    void testGetTasksByProjectIdPaginated_WithFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(testTask), pageable, 1);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(projectRepository.existsById(1L)).thenReturn(true);
        when(projectRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(testProject));
        when(taskRepository.findByProjectIdWithFilters(eq(1L), eq("test"), eq(false), any(Pageable.class)))
                .thenReturn(taskPage);
        when(taskMapper.toDto(testTask)).thenReturn(taskResponse);

        // Act
        PageResponse<TaskResponse> result = taskService.getTasksByProjectIdPaginated(1L, 0, 10, "test", false);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(taskRepository, times(1)).findByProjectIdWithFilters(eq(1L), eq("test"), eq(false), any(Pageable.class));
    }

    @Test
    void testUpdateTask_Success() {
        // Arrange
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");
        updateRequest.setDescription("Updated Description");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse updatedResponse = TaskResponse.builder()
                .id(1L)
                .title("Updated Task")
                .description("Updated Description")
                .completed(false)
                .projectId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        when(taskMapper.toDto(testTask)).thenReturn(updatedResponse);

        // Act
        TaskResponse result = taskService.updateTask(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask_NotFound() {
        // Arrange
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTask(1L, updateRequest);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTask_AccessDenied() {
        // Arrange
        User otherUser = User.builder().id(2L).email("other@example.com").build();
        Project otherProject = Project.builder().id(2L).user(otherUser).build();
        Task otherTask = Task.builder().id(2L).project(otherProject).build();

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("Updated Task");

        when(taskRepository.findById(2L)).thenReturn(Optional.of(otherTask));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.updateTask(2L, updateRequest);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskResponse updatedResponse = TaskResponse.builder()
                .id(1L)
                .title("Test Task")
                .completed(true)
                .projectId(1L)
                .createdAt(LocalDateTime.now())
                .build();
        when(taskMapper.toDto(testTask)).thenReturn(updatedResponse);

        // Act
        TaskResponse result = taskService.updateTaskStatus(1L, true);

        // Assert
        assertNotNull(result);
        assertTrue(result.isCompleted());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.updateTaskStatus(1L, true);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteTask_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        doNothing().when(taskRepository).delete(testTask);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    void testDeleteTask_NotFound() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            taskService.deleteTask(1L);
        });
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void testDeleteTask_AccessDenied() {
        // Arrange
        User otherUser = User.builder().id(2L).email("other@example.com").build();
        Project otherProject = Project.builder().id(2L).user(otherUser).build();
        Task otherTask = Task.builder().id(2L).project(otherProject).build();

        when(taskRepository.findById(2L)).thenReturn(Optional.of(otherTask));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            taskService.deleteTask(2L);
        });
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
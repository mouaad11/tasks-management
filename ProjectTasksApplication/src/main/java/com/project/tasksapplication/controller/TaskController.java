package com.project.tasksapplication.controller;

import com.project.tasksapplication.dto.request.TaskRequest;
import com.project.tasksapplication.dto.response.PageResponse;
import com.project.tasksapplication.dto.response.TaskResponse;
import com.project.tasksapplication.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request) {
        return new ResponseEntity<>(taskService.createTask(projectId, request), HttpStatus.CREATED);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<?> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false, defaultValue = "false") boolean paginated) {
        if (paginated) {
            return ResponseEntity.ok(taskService.getTasksByProjectIdPaginated(projectId, page, size, search, completed));
        }
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @PatchMapping("/tasks/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam boolean completed) {
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, completed));
    }

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }
}
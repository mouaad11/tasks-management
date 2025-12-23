package com.project.tasksapplication.mapper;

import com.project.tasksapplication.dto.request.RegisterRequest;
import com.project.tasksapplication.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        return User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .build();
    }
}
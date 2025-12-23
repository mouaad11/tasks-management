package com.project.tasksapplication.repository;

import com.project.tasksapplication.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    List<Project> findByUserId(Long userId);
    
    Page<Project> findByUserId(Long userId, Pageable pageable);

    Optional<Project> findByIdAndUserId(Long id, Long userId);
}

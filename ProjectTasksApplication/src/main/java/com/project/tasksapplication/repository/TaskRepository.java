package com.project.tasksapplication.repository;

import com.project.tasksapplication.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findByProjectId(Long projectId);
    
    Page<Task> findByProjectId(Long projectId, Pageable pageable);
    
    @Query(value = "SELECT * FROM tasks t WHERE t.project_id = :projectId AND " +
           "(:search IS NULL OR LOWER(CAST(t.title AS TEXT)) LIKE '%' || LOWER(CAST(:search AS TEXT)) || '%' OR " +
           "(t.description IS NOT NULL AND LOWER(CAST(t.description AS TEXT)) LIKE '%' || LOWER(CAST(:search AS TEXT)) || '%')) AND " +
           "(:completed IS NULL OR t.completed = :completed) " +
           "ORDER BY t.created_at DESC",
           nativeQuery = true,
           countQuery = "SELECT COUNT(*) FROM tasks t WHERE t.project_id = :projectId AND " +
                   "(:search IS NULL OR LOWER(CAST(t.title AS TEXT)) LIKE '%' || LOWER(CAST(:search AS TEXT)) || '%' OR " +
                   "(t.description IS NOT NULL AND LOWER(CAST(t.description AS TEXT)) LIKE '%' || LOWER(CAST(:search AS TEXT)) || '%')) AND " +
                   "(:completed IS NULL OR t.completed = :completed)")
    Page<Task> findByProjectIdWithFilters(
            @Param("projectId") Long projectId,
            @Param("search") String search,
            @Param("completed") Boolean completed,
            Pageable pageable);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    long countByProjectId(Long projectId);
    long countByProjectIdAndCompleted(Long projectId, boolean completed);

}

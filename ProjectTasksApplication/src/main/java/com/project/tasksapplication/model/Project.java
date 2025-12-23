package com.project.tasksapplication.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title ;

    @Column(columnDefinition = "TEXT")
    private String description ;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id" , nullable = false)
    @ToString.Exclude
    private User user;


    @OneToMany(mappedBy = "project"  , cascade = CascadeType.ALL , orphanRemoval = true)
    @ToString.Exclude
    private List<Task> tasks = new ArrayList<>();


    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt ;

    @UpdateTimestamp
    private LocalDateTime updatedAt ;


}

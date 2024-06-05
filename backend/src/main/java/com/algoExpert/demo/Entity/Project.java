package com.algoExpert.demo.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer projectId;

    @NotBlank(message = "project title required")
    private String title;

    private String description;
    @Transient
    private Long tableCount;
    private String sortDirection = Sort.Direction.ASC.name();
    private String createdDate;

    private LocalDate created_at;

    //  relationships
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskContainer> tables;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> memberList;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

}

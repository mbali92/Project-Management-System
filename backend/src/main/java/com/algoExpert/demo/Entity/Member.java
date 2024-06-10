package com.algoExpert.demo.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer member_id;

    private Integer user_id;

    private Integer projectId;

    private String username;

    private String projectRole;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Task> taskList;

}

package com.algoExpert.demo.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TaskContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tableId;
    private String tableName;
    private int projectId;

    @OneToMany( cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;


}

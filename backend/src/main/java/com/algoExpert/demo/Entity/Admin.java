package com.algoExpert.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer admin_id;
    private int number_of_owners;
    private int number_of_members;
    private int number_of_projects;
    private int number_of_tables;
    private int number_of_tasks;
//    private List<Integer> number_of_projectspermonth;
    private  int create_project_count;
    private  int create_table_count;
    private  int create_task_count;
    private  int create_taskDuplicate_count;
    private  int create_taskDelete_count;
    private  int create_sort_count;





}

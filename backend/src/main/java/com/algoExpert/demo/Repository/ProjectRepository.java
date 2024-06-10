package com.algoExpert.demo.Repository;

import com.algoExpert.demo.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository  extends JpaRepository<Project, Integer> {

    @Query("SELECT COUNT(p) FROM Project p")
    Integer countProjects();

    @Query("SELECT MONTH(p.created_at), COUNT(p) FROM Project p GROUP BY MONTH(p.created_at)")
    List<Object[]> projectsPerMonth();

}
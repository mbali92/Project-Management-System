package com.algoExpert.demo.Repository;

import com.algoExpert.demo.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    @Query("SELECT t FROM Task t WHERE t.end_date = ?1 AND t.status!= ?2")
    List<Task> findTasksDueDate(String dueDay, String status);

    @Query("SELECT COUNT(t) FROM Task t")
    Integer countTasks();

}

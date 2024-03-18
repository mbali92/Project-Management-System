package com.algoExpert.demo.Service;
import com.algoExpert.demo.Entity.Project;
import com.algoExpert.demo.Entity.Tables;
import com.algoExpert.demo.Entity.Task;
import com.algoExpert.demo.Repository.ProjectRepository;
import com.algoExpert.demo.Repository.TableRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ProjectRepository projectRepository;

//  create a new table
    public Project createTable(int project_id, int member_id){
        Project project =  projectRepository.findById(project_id).get();

        List<Tables> tables = project.getTables();
        Tables table =new Tables(0,"New Table",null);
        Task task=new Task(0,"",""
                ,member_id,"","","","",null);


        tables.add(table);
        project.setTables(tables);
        List<Task> taskList=new ArrayList<>();
        taskList.add(task);
        table.setTasks(taskList);

        return projectRepository.save(project);
    }
    @Transactional
    public List<Tables> deleteTable(Integer project_id, Integer table_id) {
        Project project = projectRepository.findById(project_id).orElse(null);
        Tables table = tableRepository.findById(table_id).orElse(null);

        List<Tables> tablesList = project.getTables();
        tablesList.remove(table);
        project.setTables(tablesList);

        projectRepository.save(project);
        return tableRepository.findAll();
    }


//  get all tables
    public List<Tables> getAllTables() {
        return tableRepository.findAll();
    }

//  update table by id
//    public Table updateTable(int id, String table) {
//        Table table1 = tableRepository.findById(id).get();
//        table1.getTable_name();
//            return tableRepository.save(table);
//    }

//    delete table by id
}


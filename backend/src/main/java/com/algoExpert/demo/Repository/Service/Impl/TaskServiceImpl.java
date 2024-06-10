package com.algoExpert.demo.Repository.Service.Impl;


import com.algoExpert.demo.Admin.AdminEnums.FeatureType;
import com.algoExpert.demo.Admin.Repository.Service.FeatureService;
import com.algoExpert.demo.AppNotification.EmailHtmlLayout;
import com.algoExpert.demo.Entity.*;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Mapper.TaskMapper;
import com.algoExpert.demo.Repository.*;
import com.algoExpert.demo.Repository.Service.TaskService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.algoExpert.demo.AppUtils.AppConstants.TABLE_NOT_FOUND;
import static com.algoExpert.demo.AppUtils.AppConstants.TASK_NOT_FOUND;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TableRepository tableRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProjectUserImpl projectUser;
    @Autowired
    private  UserRepository userRepository;


    @Autowired
    private FeatureService featureService;

    //    create new task
    @Transactional
    @Override
    public TaskContainer createTask(int table_id) throws InvalidArgument {

        TaskContainer table = tableRepository.findById(table_id).orElseThrow(() ->
                new InvalidArgument(String.format(TASK_NOT_FOUND,table_id)));

        String projectName = table.getTasks()
                .stream().map(Task::getProjectName).findFirst().orElseThrow();
        String username =  userRepository.findById(projectUser.loggedInUserId()).orElseThrow().getFullName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = null;

        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal (authenticated user)
            loggedUser = (User) authentication.getPrincipal();
        }

        List<Task> taskList = table.getTasks();
        Task task = Task.builder()
                .title("Task")
                .description("Description+")
                .username(username)
                .status("TODO")
                .start_date(" ")
                .end_date(" ")
                .projectName(projectName).build();

        FeatureType[] featureTypesToUpdate1 = {FeatureType.CREATE_TASK};
        for (FeatureType featureType : featureTypesToUpdate1) {
            featureService.updateFeatureCount(featureType);
        }

        taskList.add(task);
        table.setTasks(taskList);
        System.err.println(projectName);
        return tableRepository.save(table);
    }
    //    get all tasks
    @Override
    public List<Task> getAllTask() {
        return taskRepository.findAll();
    }

    //    update task
    @Override
    public Task editTask(Task newTask){
        return   taskRepository.findById(newTask.getTask_id())
                .map(existingTask ->{
                    Optional.ofNullable(newTask.getTitle()).ifPresent(existingTask::setTitle);
                    Optional.ofNullable(newTask.getDescription()).ifPresent(existingTask::setDescription);
                    Optional.ofNullable(newTask.getStart_date()).ifPresent(existingTask::setStart_date);
                    Optional.ofNullable(newTask.getEnd_date()).ifPresent(existingTask::setEnd_date);
                    Optional.ofNullable(newTask.getStatus()).map(String::toUpperCase).ifPresent(existingTask::setStatus);
                    Optional.ofNullable(newTask.getPriority()).map(String::toUpperCase).ifPresent(existingTask::setPriority);
                    return taskRepository.save(existingTask);
                }).orElseThrow(() -> new IllegalArgumentException(String.format(TASK_NOT_FOUND,newTask.getTask_id())));
    }

    //duplicate task
    @Transactional
    @Override
    public TaskContainer duplicateTask(Task task, Integer table_id) {
        TaskContainer table = tableRepository.findById(table_id).get();

        Task newTask = Task.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .username(task.getUsername())
                .start_date(task.getStart_date())
                .end_date(task.getEnd_date())
                .status(task.getStatus())
                .priority(task.getPriority())
                .projectName(task.getProjectName()).build();

        List<Task> taskList = table.getTasks();

        FeatureType[] featureTypesToUpdate1 = {FeatureType.DUPLICATE};
        for (FeatureType featureType : featureTypesToUpdate1) {
            featureService.updateFeatureCount(featureType);
        }

        taskList.add(newTask);
        table.setTasks(taskList);
        return tableRepository.save(table);
    }

    //  delete task
    @Override
    @Transactional
    public TaskContainer deleteTaskById(Integer task_id, Integer table_id) throws InvalidArgument {
        Task storedTask = taskRepository.findById(task_id).orElseThrow(() ->
                new InvalidArgument(String.format(TASK_NOT_FOUND,task_id)));
        TaskContainer table = tableRepository.findById(table_id).orElseThrow(() ->
                new InvalidArgument(String.format(TABLE_NOT_FOUND,table_id)));

        List<Comment> comments = storedTask.getComments();
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                commentRepository.delete(comment);
            }
        }
        List<Task> taskList = table.getTasks();
        taskList.remove(storedTask);
        table.setTasks(taskList);

        FeatureType[] featureTypesToUpdate1 = {FeatureType.DELETE_TASK};
        for (FeatureType featureType : featureTypesToUpdate1) {
            featureService.updateFeatureCount(featureType);
        }



        tableRepository.save(table);
        return table;
    }

    @Override
    public Task getTaskById(int taskId) {
        return taskRepository.findById(taskId).orElseThrow();
    }

    @Override
    public List<Task> findTaskByDateAndStatus(String date, String status) {
        return taskRepository.findTasksDueDate(date,status);
    }


}


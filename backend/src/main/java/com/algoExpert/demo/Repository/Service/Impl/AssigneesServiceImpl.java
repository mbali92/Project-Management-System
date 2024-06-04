package com.algoExpert.demo.Repository.Service.Impl;

import com.algoExpert.demo.Admin.AdminEnums.FeatureType;
import com.algoExpert.demo.Admin.Repository.Service.FeatureService;
import com.algoExpert.demo.AppNotification.AppEmailBuilder;
import com.algoExpert.demo.AppNotification.EmailHtmlLayout;
import com.algoExpert.demo.Entity.*;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Repository.*;
import com.algoExpert.demo.Repository.Service.AssigneesService;
import com.algoExpert.demo.Repository.Service.UserNotificationService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.algoExpert.demo.AppUtils.AppConstants.*;

@Service
@Slf4j
public class AssigneesServiceImpl implements AssigneesService {

    @Autowired
    private AssigneesRepository assigneesRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private AppEmailBuilder appEmailBuilder;
    @Autowired
    private EmailHtmlLayout emailHtmlLayout;
    @Autowired
    private UserNotificationService notificationService;
    @Autowired
    private FeatureService featureService;
    @Value("${task.invite.url}")
    String taskInviteLink;


    //	 Assign a task to member
    @Transactional
    @Override
    public Task assignTaskToMember(int member_id, int task_id) throws InvalidArgument {
        // check if member and task exist
        Task storedTask = taskRepository.findById(task_id).orElseThrow(() ->
                new InvalidArgument(String.format(TASK_NOT_FOUND,task_id)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = null;

        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal (authenticated user)
            loggedUser = (User) authentication.getPrincipal();
        }


        String email = memberRepository.findById(member_id)
                .orElseThrow(()->new InvalidArgument(String.format(MEMBER_NOT_FOUND,member_id)))
                .getUsername();

        int use_id = memberRepository.findById(member_id)
                .orElseThrow(()->new InvalidArgument(String.format(MEMBER_NOT_FOUND,member_id)))
                .getUser_id();
        User assignedUser = userRepository.getReferenceById(use_id);

        Assignee assignee = new Assignee(0, member_id, task_id, email);

        List<Assignee> assigneeList= storedTask.getAssignees();
        boolean assigneeExist = assigneeList
                .stream().map(Assignee::getMember_id)
                .anyMatch(id->id.equals(member_id));

        if (assigneeExist){
            throw new InvalidArgument(String.format(ALREADY_ASSIGNED,member_id));
        }

        assigneeList.add(assignee);
        storedTask.setAssignees(assigneeList);
        Task saved = taskRepository.save(storedTask);
        sendInvite(storedTask,assignedUser,storedTask.getProjectName(),email);

        //************************** create and save notification ******************************//
        String notifMsg = "You have been assigned "+storedTask.getTitle()+ " task on "+ storedTask.getProjectName() +" project";
        notificationService.createNotification(assignedUser,notifMsg);

        //************************** call feature service **************************************//
        featureService.updateFeatureCount(FeatureType.ASSIGN_TASK);

        return saved ;
    }


    //	get all assignees
    @Override
    public List<Assignee> getAllAssignees() {
       return assigneesRepository.findAll();

    }

    /**
     * Sends an invitation email to a user for a specific task.
     *
     * @param task The task for which the invitation is being sent.
     * @param user The user to whom the invitation is being sent.
     * @param projectName The name of the project to which the task belongs.
     * @param email The email address of the recipient user.
     * @Author Santos Rafaelo
     */
    private void  sendInvite(Task task, User user, String projectName,String email){
        String link = taskInviteLink +task.getTask_id();
        String subject = "PMS Task Assignment";
        String deadline = "";

        if (task.getEnd_date().isEmpty()) deadline = "Not Specified";
        else deadline = task.getEnd_date();
        //todo change TEMP_USER_EMAIL to email by passing it as an argument in sendInvite(Task task, User user, String projectName,String email) email= user.getEmail()

        String assignTaskHtml = emailHtmlLayout.assignTaskHtml(user.getFullName(), task.getTitle(), projectName, link,deadline);
        appEmailBuilder.sendEmailInvite(email,assignTaskHtml,subject);

        log.info(user.getFullName()+" {} :",link);

    }
    @Override
    public List<Assignee> getAssigneeBrTaskID(int taskId){
        return assigneesRepository.findByTaskId(taskId);
    }

}
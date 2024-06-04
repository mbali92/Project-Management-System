package com.algoExpert.demo.Repository.Service.Impl;

import com.algoExpert.demo.AppNotification.AppEmailBuilder;
import com.algoExpert.demo.AppNotification.EmailHtmlLayout;
import com.algoExpert.demo.Entity.*;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Repository.MemberRepository;
import com.algoExpert.demo.Repository.ProjectRepository;
import com.algoExpert.demo.Repository.Service.*;
import com.algoExpert.demo.Repository.TableRepository;
import com.algoExpert.demo.Repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TableService tableService;

    @Autowired
    private MemberService memberService;
    @Autowired
    private AppEmailBuilder appEmailBuilder;
    @Autowired
    private EmailHtmlLayout emailHtmlLayout;
    @Value("${project.invite.homepage.url}")
    StringBuilder projectUrl;
    @Autowired
    ProjectUserService projectUser;
    private static final String OWNER = "OWNER";
    //  create project
    @Transactional
    @Override
    public Integer createProject(Project project) throws InvalidArgument, MessagingException, IOException {
        // Find user by id
        User user = userRepository.findById(projectUser.loggedInUserId())
                .orElseThrow(() -> new InvalidArgument("User with ID " + projectUser.loggedInUserId() + " not found"));


        project.setUser(user);

        // Save the project and retrieve the saved instance
        Project savedProject = projectRepository.save(project);

        // Create a default table using new member id
        tableService.createTable(savedProject.getProjectId());

        List<Member> members = savedProject.getMemberList();
        if (members == null) {
            members = new ArrayList<>();
        }

        Member newMember = Member.builder()
                .user_id(user.getUser_id())
                .project_id(savedProject.getProjectId())
                .username(user.getUsername())
                .projectRole(OWNER)
                .build();

        members.add(newMember);
        savedProject.setMemberList(members);
        projectRepository.save(savedProject);
        String link = String.valueOf(projectUrl);
        // Save the updated project with the added member
        savedProject = projectRepository.save(savedProject);
//        memberService.inviteMember(savedProject.getProject_id(),user.getUser_id());

        String subject = "PMS Project Created Successful";
        String projectHtml = emailHtmlLayout.createProjectHtml(user.getFullName()
                , savedProject.getTitle()
                , link);
        //todo change TEMP_USER_EMAIL to user.getEmail()
        appEmailBuilder.sendEmailInvite(user.getEmail(),projectHtml,subject);

        return savedProject.getProjectId();
    }

    //  get all projects
    @Override
    public List<Project> getAllProjects() {
      return projectRepository.findAll();
    }
    //get one project

    //    delete project
    @Override
    public List<Project> deleteProjectById(Integer projectId) throws InvalidArgument {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new InvalidArgument("Project with ID " + projectId + " not found"));

        // Delete associated tables
        for (TaskContainer table : project.getTables()) {
            tableRepository.delete(table);
        }

        // Delete associated members
        for (Member member : project.getMemberList()) {
            memberRepository.delete(member);
        }
        // Now delete the project
        projectRepository.delete(project);

        return projectRepository.findAll();
    }

    //    update project
    @Override
    public Project editProject(Project newProjectValue) throws InvalidArgument{
        return projectRepository.findById(newProjectValue.getProjectId())
                .map(existingProject -> {
                    Optional.ofNullable(newProjectValue.getTitle()).ifPresent(existingProject::setTitle);
                    Optional.ofNullable(newProjectValue.getDescription()).ifPresent(existingProject::setDescription);
                    Optional.ofNullable(newProjectValue.getSortDirection()).ifPresent(existingProject::setSortDirection);
                    return projectRepository.save(existingProject);
                }).orElseThrow(() -> new InvalidArgument("Task with ID " + newProjectValue.getProjectId() + " not found"));

    }

}
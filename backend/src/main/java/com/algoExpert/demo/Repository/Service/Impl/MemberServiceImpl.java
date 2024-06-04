package com.algoExpert.demo.Repository.Service.Impl;


import com.algoExpert.demo.Admin.AdminEnums.FeatureType;
import com.algoExpert.demo.Admin.Repository.Service.FeatureService;
import com.algoExpert.demo.AppNotification.AppEmailBuilder;
import com.algoExpert.demo.AppNotification.EmailHtmlLayout;
import com.algoExpert.demo.Entity.Member;
import com.algoExpert.demo.Entity.Project;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Repository.MemberRepository;
import com.algoExpert.demo.Repository.ProjectRepository;
import com.algoExpert.demo.Repository.Service.MemberService;
import com.algoExpert.demo.Repository.Service.UserNotificationService;
import com.algoExpert.demo.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.algoExpert.demo.AppUtils.AppConstants.*;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AppEmailBuilder appEmailBuilder;
    @Autowired
    private EmailHtmlLayout emailHtmlLayout;
    @Autowired
    private UserNotificationService notificationService;

    @Autowired
    private FeatureService featureService;
    @Value("${project.invite.homepage.url}")
    StringBuilder projectUrl;
       public static final String MEMBER = "MEMBER";



    //    Invite member to project

    @Override
    public Member inviteMember(int project_id, int user_id) throws InvalidArgument {
        // check if user and project exist
        User user = userRepository.findById(user_id).orElseThrow(() ->
                new InvalidArgument(String.format(USER_NOT_FOUND,user_id)));

        Project userProject = projectRepository.findById(project_id).orElseThrow(() ->
                new InvalidArgument(String.format(PROJECT_NOT_FOUND,project_id)));


        List<Member> members = userProject.getMemberList();

        // check if member exist
        boolean memberExist = members.stream()
                .map(Member::getUser_id)
                .anyMatch(id -> id == user_id);

        if (memberExist) {
            throw new InvalidArgument(String.format(ALREADY_A_MEMBER,user_id));
        } else {

            Member newMember = Member.builder()
                    .user_id(user.getUser_id())
                    .project_id(project_id)
                    .username(user.getUsername())
                    .projectRole(MEMBER)
                    .build();

            members.add(newMember);
            userProject.setMemberList(members);
            projectRepository.save(userProject);

            String subject = "PMS Project Invitation";
                String projectHtml = emailHtmlLayout.inviteToProjectHtml(user.getFullName()
                        , userProject.getTitle()
                        , String.valueOf(projectUrl)
                        ,userProject.getUser().getFullName());
                //todo change TEMP_USER_EMAIL to user.getEmail()
                appEmailBuilder.sendEmailInvite(user.getEmail(),projectHtml,subject);

            //************************** create and save notification ******************************//
            String projectOwner = userProject.getUser().getFullName();
            String projectName = userProject.getTitle();

            String notifMsg = projectOwner + " added you to project " + projectName;
            notificationService.createNotification(user,notifMsg);

            log.info("You have been invited to the project {} :",projectUrl);
            //************************** call feature service ******************************//
            featureService.updateFeatureCount(FeatureType.INVITE_MEMBER);
            return memberRepository.save(newMember);
        }


    }


    //    get all members
    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public List<User> searchMemberToInvite(String fullnameLetters) {

        //************************** call feature service ******************************//
        featureService.updateFeatureCount(FeatureType.SEARCH);
        return userRepository.findByFullName(fullnameLetters);
    }

    //    get member id
    @Override
    public Integer findLoginMember(int user_id) {
        List<Member> memberList = memberRepository.findAll();

        Optional<Member> optionalMember = memberList.stream()
                .filter(member -> member.getUser_id() == user_id)
                .findFirst();

        if (optionalMember.isPresent()) {
            return optionalMember.get().getMember_id();
        } else {
            // Handle case when member with given user_id is not found
            return null; // Or throw an exception, depending on your use case
        }
    }

}
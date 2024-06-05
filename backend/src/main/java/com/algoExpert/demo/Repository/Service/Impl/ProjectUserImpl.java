package com.algoExpert.demo.Repository.Service.Impl;

import com.algoExpert.demo.Admin.AdminEnums.FeatureType;
import com.algoExpert.demo.Admin.Repository.FeatureUsageRepository;
import com.algoExpert.demo.Admin.Repository.Service.FeatureService;
import com.algoExpert.demo.Admin.Repository.Service.Impl.FeatureServiceImpl;
import com.algoExpert.demo.Entity.*;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Jwt.JwtResponse;
import com.algoExpert.demo.Jwt.JwtService;
import com.algoExpert.demo.Repository.*;
import com.algoExpert.demo.Repository.Service.ProjectUserService;
import com.algoExpert.demo.role.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.algoExpert.demo.AppUtils.AppConstants.*;

@Service
@Slf4j
public class ProjectUserImpl implements ProjectUserService {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private RefreshTokenSevice refreshTokenSevice;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private FeedbackRepo feedbackRepo;


    @Autowired
    private FeatureService featureService;

    @Autowired
    private FeatureUsageRepository featureUsageRepository;


    @Override
    public Project findProject(int project_id) throws InvalidArgument {
        Project foundProject= projectRepository.findById(project_id).orElseThrow(() -> new InvalidArgument(String.format(PROJECT_NOT_FOUND,project_id)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User projectUser = null;

        if (authentication != null) {
            projectUser = (User) authentication.getPrincipal();
        }

        //clear the roles of the user who is loading the project
        List<Role> roleList = projectUser.getRoles();
        roleList.clear();
        log.info("clear roles:{} ",roleList);
//        //assign the role of the user that all users should have
        roleList.add(Role.valueOf(USER_ROLE));
        log.info("add roles:{} ",roleList);
        for (Member member : foundProject.getMemberList() ){
            if(member.getProjectRole().equals(OWNER_ROLE) && member.getUser_id().equals(projectUser.getUser_id()) ){
                //find if the member has a role of an onwer and assign owner role if you find
                roleList.add(Role.valueOf(OWNER_ROLE));
            }
            else if(member.getUser_id().equals(projectUser.getUser_id()) && member.getProjectRole().equals(MEMBER_ROLE)){
                //assign role of a member if you find the id matching the user id who is loading the project
                roleList.add(Role.valueOf(MEMBER_ROLE));
            }
        }
        projectUser.setRoles(roleList);
        userRepository.save(projectUser);
        return foundProject;
    }

    @Override
    public ResponseEntity<Project> getProjectWithSortedAndSearchedTables(int projectId, int page, int size, String search, String sortField, String sortDirection) {
        Project foundProject= projectRepository.findById(projectId).orElseThrow(() -> new InvalidArgument(String.format(PROJECT_NOT_FOUND,projectId)));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User projectUser = null;

        if (authentication != null) {
            projectUser = (User) authentication.getPrincipal();
        }


        List<Role> roleList = Objects.requireNonNull(projectUser).getRoles();
        roleList.clear();

        roleList.add(Role.valueOf(USER_ROLE));
        for (Member member : foundProject.getMemberList() ){
            if(member.getProjectRole().equals(OWNER_ROLE) && member.getUser_id().equals(projectUser.getUser_id()) ){

                roleList.add(Role.valueOf(OWNER_ROLE));
            }
            else if(member.getUser_id().equals(projectUser.getUser_id()) && member.getProjectRole().equals(MEMBER_ROLE)){
                roleList.add(Role.valueOf(MEMBER_ROLE));
            }
        }
        projectUser.setRoles(roleList);
        userRepository.save(projectUser);
        getSortedAndSearchedTables(foundProject,page,size,search,sortField,sortDirection);
        return ResponseEntity.ok(foundProject);
    }



    @Override
    public JwtResponse refreshJwtToken(String refreshTokenRequest){
        return  refreshTokenRepository.findByToken(refreshTokenRequest)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user.getUsername());
                    String refreshToken = refreshTokenSevice.createRefreshToken(user.getUsername()).getToken();
                    return  JwtResponse.builder()
                            .jwtToken(accessToken)
                            .refreshToken(refreshToken).build();
                }).orElseThrow(() ->new RuntimeException(
                        "Refresh token is not in database"
                ));
    }

    @Override
    public Page<User> getUsersWithPagination(int offset, int pageSize) {
        return userRepository.findAll(PageRequest.of(offset,pageSize));
    }


    public void getSortedAndSearchedTables(Project project, int page, int size, String search, String sortField, String sortDirection){

        Sort.Direction direction = sortDirection.equalsIgnoreCase("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);


            // If the FeatureUsage table is not empty, update only the CREATE_PROJECT feature type
            FeatureType[] featureTypesToUpdate = {FeatureType.SORT};
            for (FeatureType featureType : featureTypesToUpdate) {
                featureService.updateFeatureCount(featureType);
            }


        List<TaskContainer> tables;

        tables = (search != null) ?
                tableRepository.findByProjectIdAndTableNameContainingIgnoreCase(project.getProjectId(),search,pageable).getContent() :
                tableRepository.findByProjectId(project.getProjectId(),pageable).getContent();

        project.setTableCount(tableRepository.countByProjectId(project.getProjectId()));
        project.setTables(tables);
    }


    @Override
    public Integer loggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedUser = null;

        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal (authenticated user)
            loggedUser = (User) authentication.getPrincipal();
        }
        return Objects.requireNonNull(loggedUser).getUser_id();
    }

    @Override
    public List<Project> getUserProjectIds() {
        // Find all members
        List<Member> memberList = memberRepository.findAll();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User projectUser = null;

        if (authentication != null) {
            projectUser = (User) authentication.getPrincipal();
        }

        User systemUser = userRepository.findById(projectUser.getUser_id()).get();

        // Filter members by user_id and map them to project ids
        List<Integer> userProjectIds = memberList.stream()
                .filter(member -> member.getUser_id() == systemUser.getUser_id())
                .map(Member::getProjectId) // Assuming you have a method getProject_id() in Member class
                .collect(Collectors.toList());

        return projectRepository.findAllById(userProjectIds);
    }
    //send user feedback
    @Override
    public String sendUserFeedback(Feedback userFeedback) {
        try {
            feedbackRepo.save(userFeedback);
            return "feedback successfully saved";
        }catch (Exception e){
            return "feedback could not be saved" + e;
        }
    }



//    @Override
//    public User getUserById(int userId) {
//        return userRepository.findById(userId).orElseThrow();
//    }




}

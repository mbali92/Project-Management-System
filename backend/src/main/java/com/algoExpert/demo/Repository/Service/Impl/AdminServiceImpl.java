package com.algoExpert.demo.Repository.Service.Impl;

import com.algoExpert.demo.Admin.AdminEnums.FeatureType;
import com.algoExpert.demo.Admin.Repository.FeatureUsageRepository;
import com.algoExpert.demo.Admin.Repository.Service.FeatureService;
import com.algoExpert.demo.Entity.Admin;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.Repository.*;
import com.algoExpert.demo.Repository.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private  MemberRepository memberRepository;

    @Autowired
    private  AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;


    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FeatureUsageRepository featureUsageRepository;

    @Autowired
    private FeatureService featureService;

    @Override
    public Admin getUsersStats() {
        Optional<Admin> adminOne = adminRepository.findById(1);

        Admin admin;
        if(adminOne.isPresent()){
            admin = adminOne.get();
        }else {
            admin = new Admin();
        }
        admin.setNumber_of_owners(memberRepository.totalProjectOwners());
        admin.setNumber_of_members(memberRepository.totalProjectMembers());
        return adminRepository.save(admin);
    }

    @Override
    public List<Object[]> newUsers() {
        return userRepository.getNewUsers(2024);
    }


    @Override
    public Page<User> AdminUserPagination(Integer offset, Integer pageSize) {
        return   userRepository.findAll(PageRequest.of(offset,pageSize));
    }

    @Override
    public Admin getProjectStats() {
        Optional<Admin> adminOne = adminRepository.findById(1);

        Admin admin;
        if(adminOne.isPresent()){
            admin = adminOne.get();
        }else {
            admin = new Admin();
        }

        admin.setNumber_of_projects(projectRepository.countProjects());
        admin.setNumber_of_tables(tableRepository.countTables());
        admin.setNumber_of_tasks(taskRepository.countTasks());

        return adminRepository.save(admin);
    }

    @Override
    public List<Object[]> projectsCreatedPerMonth() {
        return projectRepository.projectsPerMonth();
    }

    @Override
    public Admin getProjectUsageCount() {
        Optional<Admin> adminOne = adminRepository.findById(1);

        Admin admin;
        if(adminOne.isPresent()){
            admin = adminOne.get();
        }else {
            admin = new Admin();
        }

       admin.setCreate_project_count(featureUsageRepository.countProjectUsage(FeatureType.CREATE_PROJECT));
        admin.setCreate_table_count(featureUsageRepository.countTableUsage(FeatureType.CREATE_TABLE));
        admin.setCreate_task_count(featureUsageRepository.countTaskUsage(FeatureType.CREATE_TASK));
        admin.setCreate_taskDuplicate_count(featureUsageRepository.countDuplicateUsage(FeatureType.DUPLICATE));
        admin.setCreate_taskDelete_count(featureUsageRepository.countDeleteTaskUsage(FeatureType.DELETE_TABLE));
        admin.setCreate_sort_count(featureUsageRepository.countSortUsage(FeatureType.SORT));

        return adminRepository.save(admin);

    }


}

package com.algoExpert.demo.Repository.Service;

import com.algoExpert.demo.Entity.Admin;
import com.algoExpert.demo.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AdminService {
  Admin getUsersStats();
  List<Object[]>newUsers();

  public Page<User> AdminUserPagination(Integer offset , Integer pageSize );

  Admin getProjectStats();

  List<Object[]> projectsCreatedPerMonth();

  Admin getProjectUsageCount();








}

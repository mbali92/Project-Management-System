package com.algoExpert.demo.Controller;

import com.algoExpert.demo.Entity.Admin;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.Repository.Service.AdminService;
import com.algoExpert.demo.UserAccount.AccountInfo.Service.Impl.AccountConfirmationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;


    @Autowired
    private AccountConfirmationServiceImpl confirmationService;

    @GetMapping("/usersStats")
    public Admin findUsersStats(){
        return adminService.getUsersStats();
    }

    @GetMapping("/projectStats")
    public Admin findProjectStats(){
        return adminService.getProjectStats();
    }

    @GetMapping("/numberOfNewUsers")
    public List<Object[]> newUsers(){
        return  adminService.newUsers();
    }

    @GetMapping("/paginate/{offset}/{pageSize}")
    public Page<User> pagination(@PathVariable Integer offset, @PathVariable Integer pageSize){
        return   adminService.AdminUserPagination(offset,pageSize);
    }

    @PutMapping("/deactivate/{userId}")
    public String deactivateAccount(@PathVariable Integer userId) {
        return confirmationService.deactivateAccount(userId);
    }

    @PutMapping("/activate/{userId}")
    public String activateAccount(@PathVariable Integer userId) {
        return confirmationService.activateAccount(userId);
    }

    @GetMapping("/projectspermonth")
    public List<Object[]> projectPerMonth(){
        return  adminService.projectsCreatedPerMonth();
    }

    @GetMapping("/projectCount")
    public Admin ProjectCount(){
        return  adminService.getProjectUsageCount();
    }


}

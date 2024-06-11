package com.algoExpert.demo.Repository.Service;

import com.algoExpert.demo.Entity.Feedback;
import com.algoExpert.demo.Entity.Project;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Jwt.JwtResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectUserService {
    Project findProject(int project_id) throws InvalidArgument, InternalAuthenticationServiceException;
    Integer loggedInUserId();
    List<Project> getUserProjectIds();
    Page<User> getUsersWithPagination(int offset, int pageSize);
    ResponseEntity<Project> getProjectWithSortedAndSearchedTables(int projectId, int page, int size, String search, String sortField, String sortDirection);
    String sendUserFeedback(Feedback userFeedback);

}

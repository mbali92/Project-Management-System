package com.algoExpert.demo.Mapper;

import com.algoExpert.demo.Dto.ProjectDto;
import com.algoExpert.demo.Entity.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public static ProjectDto mapToProjectDto(Project project) {
        return new ProjectDto(
                project.getProjectId(),
                project.getTitle(),
                project.getDescription(),
                project.getTables(),
                project.getMemberList(),
                project.getUser()
        );
    }

//    public static Project mapToProject(ProjectDto projectDto) {
//        return new Project(
//                projectDto.getProject_id(),
//                projectDto.getTitle(),
//                projectDto.getDescription(),
//                projectDto.getTables(),
//                projectDto.getMemberList(),
//                projectDto.getUser()
//        );
//    }
}

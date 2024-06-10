package com.algoExpert.demo.Repository.Service;

import com.algoExpert.demo.Entity.Member;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface MemberService {

    @Transactional
    Member inviteMember(int project_id, int user_id) throws InvalidArgument, MessagingException, IOException;

    List<Member> getAllMembers();

    List<User>searchMemberToInvite(String fullnameLetters);
    Integer findLoginMember(int user_id);
    List<Member> findMemberByProjectId(Integer projectId);
}

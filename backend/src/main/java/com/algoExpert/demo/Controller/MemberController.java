package com.algoExpert.demo.Controller;

import com.algoExpert.demo.Entity.Member;
import com.algoExpert.demo.Entity.User;
import com.algoExpert.demo.ExceptionHandler.InvalidArgument;
import com.algoExpert.demo.Repository.Service.MemberService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

//    invite member to a project
    @GetMapping("/inviteMember/{project_id}/{user_id}")
    public Member inviteMember(@PathVariable int project_id, @PathVariable int user_id) throws InvalidArgument, MessagingException, IOException {
        return memberService.inviteMember(project_id,user_id);
    }
//    get all members of a project
    @GetMapping("/getAllMembers")
    public List<Member> getAllMembers(){
        return memberService.getAllMembers();
    }

    //search members to invite
    @PostMapping("/searchMembers")
    public List<User> searchMembers(@RequestParam String fullnameLetters){
        return memberService.searchMemberToInvite(fullnameLetters);
    }

    @GetMapping("/project/{projectId}")
    public List<Member> findMemberByProjectId(@PathVariable Integer projectId){
        return memberService.findMemberByProjectId(projectId);
    }

    @GetMapping("/getMemberId/{user_id}")
    public Integer getMemberId(@PathVariable int user_id ){
        return memberService.findLoginMember(user_id);
    }
}
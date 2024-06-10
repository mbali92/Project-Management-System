package com.algoExpert.demo.Repository;

import com.algoExpert.demo.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    @Query("SELECT COUNT(DISTINCT user_id) FROM Member WHERE projectRole = 'OWNER'")
    Integer totalProjectOwners();

    @Query("SELECT COUNT(DISTINCT user_id) FROM Member WHERE projectRole = 'MEMBER'")
    Integer totalProjectMembers();
    List<Member> findByProjectId(Integer projectId);
}

package study.jpaquerydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.jpaquerydsl.dto.MemberSearchCond;
import study.jpaquerydsl.dto.MemberTeamDto;
import study.jpaquerydsl.entity.Member;
import study.jpaquerydsl.entity.Team;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void memberTest() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        Member findMember = memberRepository.findById(member1.getId()).get();
        System.out.println("findMember = " + findMember);

        List<Member> result = memberRepository.findAll();
        for (Member member : result) {
            System.out.println("member = " + member);
        }

        List<Member> findName = memberRepository.findByName("member1");
        System.out.println("findName = " + findName);
    }

    @Test
    public void searchTest() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 16, teamA);
        Member member3 = new Member("member3", 20, teamA);
        Member member4 = new Member("member4", 30, teamB);
        Member member5 = new Member("member5", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);

        MemberSearchCond cond = new MemberSearchCond();
        cond.setAgeGoe(20);
        cond.setAgeLoe(40);
        cond.setTeamName("teamA");

        List<MemberTeamDto> findMember = memberRepository.searchByWhere(cond);
        for (MemberTeamDto memberTeamDto : findMember) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }
    @Test
    public void simplePaging() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member1", 16, teamA);
        Member member3 = new Member("member3", 20, teamA);
        Member member4 = new Member("member4", 30, teamB);
        Member member5 = new Member("member5", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);

        MemberSearchCond cond = new MemberSearchCond();
        PageRequest pageRequest = PageRequest.of(0, 3);

        Page<MemberTeamDto> result = memberRepository.searchPageSimple(cond, pageRequest);

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }

}
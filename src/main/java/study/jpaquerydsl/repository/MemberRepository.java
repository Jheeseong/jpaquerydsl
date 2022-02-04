package study.jpaquerydsl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.jpaquerydsl.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    List<Member> findByName(String username);
}

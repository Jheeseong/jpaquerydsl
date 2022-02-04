package study.jpaquerydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.jpaquerydsl.dto.MemberSearchCond;
import study.jpaquerydsl.dto.MemberTeamDto;
import study.jpaquerydsl.repository.MemberRepository;
import study.jpaquerydsl.repository.MemberRepositoryCustomImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepositoryCustomImpl memberRepositoryCustomImpl;
    private final MemberRepository memberRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCond cond) {
        return memberRepositoryCustomImpl.searchByWhere(cond);
    }
    @GetMapping("/v2/members")
    public Page<MemberTeamDto> simplePaging(MemberSearchCond cond, Pageable pageable) {
        return memberRepository.searchPageSimple(cond, pageable);
    }
    @GetMapping("/v3/members")
    public Page<MemberTeamDto> complexPaging(MemberSearchCond cond, Pageable pageable) {
        return memberRepository.searchPageComplex(cond, pageable);
    }
}

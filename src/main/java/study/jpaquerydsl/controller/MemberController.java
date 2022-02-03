package study.jpaquerydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.jpaquerydsl.dto.MemberSearchCond;
import study.jpaquerydsl.dto.MemberTeamDto;
import study.jpaquerydsl.repository.MemberRepositoryCustomImpl;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepositoryCustomImpl memberRepositoryCustomImpl;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSearchCond cond) {
        return memberRepositoryCustomImpl.searchByWhere(cond);
    }
}

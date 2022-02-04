package study.jpaquerydsl.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import study.jpaquerydsl.dto.MemberSearchCond;
import study.jpaquerydsl.dto.MemberTeamDto;

import java.util.List;

public interface MemberRepositoryCustom{

    List<MemberTeamDto> searchByBuilder(MemberSearchCond cond);

    List<MemberTeamDto> searchByWhere(MemberSearchCond cond);

    Page<MemberTeamDto> searchPageSimple(MemberSearchCond cond, Pageable pageable);

    Page<MemberTeamDto> searchPageComplex(MemberSearchCond cond, Pageable pageable);
}

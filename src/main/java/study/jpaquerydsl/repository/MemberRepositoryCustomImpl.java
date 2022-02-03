package study.jpaquerydsl.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryFactory;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import study.jpaquerydsl.dto.MemberSearchCond;
import study.jpaquerydsl.dto.MemberTeamDto;
import study.jpaquerydsl.dto.QMemberTeamDto;
import study.jpaquerydsl.entity.QMember;
import study.jpaquerydsl.entity.QTeam;

import java.util.List;

import static study.jpaquerydsl.entity.QMember.member;
import static study.jpaquerydsl.entity.QTeam.team;

@Repository
@AllArgsConstructor
public class MemberRepositoryCustomImpl {

    private final JPAQueryFactory queryFactory;

    public List<MemberTeamDto> searchByBuilder(MemberSearchCond cond) {
        BooleanBuilder builder = new BooleanBuilder();
        if (cond.getUserName() != null) {
            builder.and(member.name.eq(cond.getUserName()));
        }
        if (cond.getTeamName() != null) {
            builder.and(team.name.eq(cond.getTeamName()));
        }
        if (cond.getAgeGoe() != null) {
            builder.and(member.age.goe(cond.getAgeGoe()));
        }
        if (cond.getAgeLoe() != null) {
            builder.and(member.age.loe(cond.getAgeLoe()));
        }
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.name,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(builder)
                .fetch();
    }

    public List<MemberTeamDto> searchByWhere(MemberSearchCond cond) {
        return queryFactory
                .select(new QMemberTeamDto(
                        member.id,
                        member.name,
                        member.age,
                        team.id,
                        team.name))
                .from(member)
                .leftJoin(member.team, team)
                .where(nameEq(cond.getUserName()),
                        teamNameEq(cond.getTeamName()),
                        ageGoe(cond.getAgeGoe()),
                        ageLoe(cond.getAgeLoe()))
                .fetch();
    }

    private BooleanExpression nameEq(String userName) {
        return userName != null ? member.name.eq(userName) : null;
    }

    private BooleanExpression teamNameEq(String teamName) {
        return teamName != null ? team.name.eq(teamName) : null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.loe(ageLoe) : null;
    }
}

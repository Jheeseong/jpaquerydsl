package study.jpaquerydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.jpaquerydsl.dto.*;
import study.jpaquerydsl.entity.Member;
import study.jpaquerydsl.entity.QMember;
import study.jpaquerydsl.entity.Team;
import study.jpaquerydsl.repository.MemberRepositoryCustomImpl;

import javax.persistence.EntityManager;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.select;
import static study.jpaquerydsl.entity.QMember.member;
import static study.jpaquerydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @Autowired
    MemberRepositoryCustomImpl memberRepositoryCustom;

    @BeforeEach
    public void Init() {
        queryFactory = new JPAQueryFactory(em);
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
    }

    @Test
    public void JPQLQuery() {
        Member findMember = em.createQuery("select m From Member m where m.age = :age", Member.class)
                .setParameter("age", 10)
                .getSingleResult();

        System.out.println("findMember = " + findMember);
    }

    @Test
    public void QueryDslTest() {
        //QMember qMember = new QMember("m");

        Member findMember = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.age.eq(10))
                .fetchOne();

        System.out.println("findMember = " + findMember);
    }

    @Test
    public void basicSearchQuery() {
        List<Member> result = queryFactory.selectFrom(member)
                .where(member.name.eq("member1"),
                        member.age.eq(10))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void sortQuery() {
        em.persist(new Member(null,60));
        em.persist(new Member("member6",60));
        em.persist(new Member("member6",60));

        List<Member> result = queryFactory
                .select(member)
                .from(member)
                .orderBy(member.age.desc(), member.name.asc().nullsLast())
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void pagingQuery() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.name.desc())
                .offset(1)
                .limit(2)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }

        QueryResults<Member> result2 = queryFactory
                .selectFrom(member)
                .orderBy(member.name.desc())
                .offset(0)
                .limit(2)
                .fetchResults();

        System.out.println("result2.getTotal() = " + result2.getTotal());
        System.out.println("result2.getLimit() = " + result2.getLimit());
        System.out.println("result2.getResults() = " + result2.getResults());
    }

    @Test
    public void aggregationQuery() {
        List<Tuple> result = queryFactory
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void groupQuery() {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .having(team.name.eq("teamB"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    //join
    @Test
    public void joinTest() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    //thetaJoin
    @Test
    public void thetaJoinTest() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .from(member, team)
                .where(member.name.eq(team.name))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    //on절 조인
    @Test
    public void on_joinTest() {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team,team).on(team.name.eq("teamA"))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void on_join_no_relation() {

        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.name.eq(team.name))
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void fetch_joinTest() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(team.name.eq("teamA"))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void subQueryTest() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(select(memberSub.age.min())
                        .from(memberSub)))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void subQueryGoe() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(select(memberSub.age.avg())
                        .from(memberSub)))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void subQueryIn() {
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(select(memberSub.age)
                        .from(memberSub)
                        .where(memberSub.age.between(10, 30))))
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void selectSubQueryTest(){
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.name, select(memberSub.age.avg())
                        .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void caseComplex() {
        StringExpression rankPath = new CaseBuilder()
                .when(member.age.between(0, 20)).then("0~20살")
                .when(member.age.between(21, 30)).then("21~30살")
                .otherwise("기타");


        List<Tuple> result = queryFactory
                .select(member.name, member.age, rankPath)
                .from(member)
                .orderBy(rankPath.asc())
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void addString() {
        List<String> result = queryFactory
                .select(member.name.concat("_").concat(member.age.stringValue()))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void projectionReturnValue() {
        List<String> oneResult = queryFactory
                .select(member.name)
                .from(member)
                .fetch();

        List<Tuple> manyResult = queryFactory
                .select(member.name, member.age)
                .from(member)
                .fetch();
    }

    @Test
    public void searchDto_setter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.name,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void searchDto_field() {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                        member.name.as("userName"),
                        ExpressionUtils.as(select(member.age.max())
                                .from(memberSub), "age")))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void searchDto_constructor() {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.name, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void booleanBuilderQuery() {
        String nameParam = "member3";
        Integer ageParam = null;

        List<Member> result = searchMember1(nameParam,ageParam);

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    private List<Member> searchMember1(String nameCond, Integer ageCond) {
        BooleanBuilder builder = new BooleanBuilder();

        if (ageCond != null) {
            builder.and(member.age.eq(ageCond));
        }

        if(nameCond != null) {
            builder.and(member.name.eq(nameCond));
        }
        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    @Test
    public void whereParamTest() {

        String nameParam = "member3";
        Integer ageParam = null;

        List<Member> result = searchMember2(nameParam,ageParam);

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }


    }

    private List<Member> searchMember2(String nameCond, Integer ageCond) {
        return  queryFactory
                .selectFrom(member)
                .where(nameEq(nameCond), ageEq(ageCond))
                .fetch();

    }

    private BooleanExpression nameEq(String nameCond) {
        return nameCond != null ? member.name.eq(nameCond) : null;
    }

    private BooleanExpression ageEq(Integer ageCond) {
        return ageCond != null ? member.age.eq(ageCond) : null;
    }

    @Test
    public void bulkQuery() {
        long update = queryFactory
                .update(member)
                .set(member.name, "비회원")
                .where(member.age.lt(20))
                .execute();

        em.flush();
        em.clear();

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void searchByRepository() {
        MemberSearchCond cond = new MemberSearchCond();
        cond.setAgeGoe(10);
        cond.setAgeLoe(30);
        cond.setTeamName("teamA");

        List<MemberTeamDto> result = memberRepositoryCustom.searchByBuilder(cond);

        for (MemberTeamDto memberTeamDto : result) {
            System.out.println("memberTeamDto = " + memberTeamDto);
        }
    }


}

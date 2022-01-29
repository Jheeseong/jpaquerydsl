# jpaquerydsl
# v1.0 1/27
## Querydsl 설정
- build.gradle에 querydsl 설정 추가

      buildscript {
	      ext {
  	    	queryDslVersion = "5.0.0"
      	}
      }

      plugins {
    	id 'org.springframework.boot' version '2.6.3'
    	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    	// querydsl 추가
    	id "com.ewerk.gradle.plugins.querydsl" version "1.0.10"
    	id 'java'
      }

      group = 'study'
      version = '0.0.1-SNAPSHOT'
      sourceCompatibility = '11'

      configurations {
      	compileOnly {
      		extendsFrom annotationProcessor
      	}
      }

      repositories {
      	mavenCentral()
      }

      dependencies {
      	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
      	implementation 'org.springframework.boot:spring-boot-starter-web'
      	// querydsl 추가
      	implementation "com.querydsl:querydsl-jpa:${queryDslVersion}"
      	implementation "com.querydsl:querydsl-apt:${queryDslVersion}"

      	compileOnly 'org.projectlombok:lombok'
      	runtimeOnly 'com.h2database:h2'
      	annotationProcessor 'org.projectlombok:lombok'
      	testImplementation 'org.springframework.boot:spring-boot-starter-test'
      }

      test {
      	useJUnitPlatform()
      }

      // querydsl 추가 시작
      def querydslDir = "$buildDir/generated/querydsl"

      querydsl {
      	jpa = true
      	querydslSourcesDir = querydslDir
      }

      sourceSets {
      	main.java.srcDir querydslDir
      }

      configurations {
      	compileOnly {
      		extendsFrom annotationProcessor
      	}
      	querydsl.extendsFrom compileClasspath
      }
    
      compileQuerydsl {
      	options.annotationProcessorPath = configurations.querydsl
      }
      // querydsl 추가 끝
      
      
### Q타입 생성
- gradle -> Tasks -> other -> compileQuerydsl 에서 Q타입 컴파일
- build -> generated -> querydsl -> study.querydsl.entity.Q-- 파일 생성 확인

# v1.1 1/28
## QueryDSL 기본 문법
### JPQL vs QueryDSL
**JPQL**

    @Test
    public void JPQLQuery() {
        Member findMember = em.createQuery("select m From Member m where m.age = :age", Member.class)
                .setParameter("age", 10)
                .getSingleResult();

        System.out.println("findMember = " + findMember);
    }
    
**QueryDSL**

    @Test
    public void QueryDslTest() {
        //QMember qMember = new QMember("m");

        Member findMember = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.age.eq(10))
                .fetchOne();

        System.out.println("findMember = " + findMember);
    }
    
**Init**

    @Autowired
    EntityManager em;

    JPAQueryFactory queryFactory;

    @BeforeEach
    public void before() {
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
    
- JPAQueryFactory를 필드로 제공하더라도 동시성 문제가 해결, JPAQeuryFactory가 생성될 때 제공되는 EntityManager에 동시성 문제가 달려 있음. 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager가 접근해도, 트랜잭젼 마다 별도의 영속성 컨텍스트를 제공하기 떄문에, 동시성 문제가 생기지 않음
- Query는 JPQL을 빌더
- JPQL은 실행 시점 오류 알림, QueryDSL은 컴파일 시점 오류 알림
- JPQL은 파라미터 바인딩을 직접, QueryDSL은 파라미터 바인딩 자동 처리
- QuertDSL이 사용하기에 유용, 동적 쿼리도 편리하게 가능

### Q-Type 활용
#### Q클래스 인스턴스 사용(2가지)
    
    QMember qMember = new QMember("m"); //별칭 직접 지정
    QMember qMember = QMember.member; //기본 인스턴스 사용
    
### 검색 쿼리

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
    
- 검색 조건은 .and(), .or(), ,(쉼표)로 메서드 연결 가능
- select와 from 은 selectFrom으로 합치는 것이 가능
- where()에 검색조건 가능, 검색 조건 추가가 가능한데, 이 때 null 값은 무시 -> 동적쿼리 
#### 검색 쿼리 문법 종류

    member.username.eq("member1") // username = 'member1'
    member.username.ne("member1") //username != 'member1'
    member.username.eq("member1").not() // username != 'member1'
    
    member.username.isNotNull() //이름이 is not null
    
    member.age.in(10, 20) // age in (10,20)
    member.age.notIn(10, 20) // age not in (10, 20)
    member.age.between(10,30) //between 10, 30
    
    member.age.goe(30) // age >= 30
    member.age.gt(30) // age > 30
    member.age.loe(30) // age <= 30
    member.age.lt(30) // age < 30
    
    member.username.like("member%") //like 검색
    member.username.contains("member") // like ‘%member%’ 검색
    member.username.startsWith("member") //like ‘member%’ 검색
    
### 결과 조회
- fetch() : 리스트 조회, 데이터 없을 시 빈 리스크 반환
- fetchone() : 단 건 조회, 데이터 없을 시 null, 둘 이상 시 exception 발생
- fetchFirst() : limit(1).fetchOne()
- fetchResults() : 페이징 정보 포함, count 쿼리 실행
- fetchCount() : count 쿼리로 변경하여 count 수 조회

# v1.1 1/29
## QueryDSL 기본 문법
### 정렬

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
    
- desc() : 내림차순, asc() : 올림차순
- nullsLast() : null 마지막, nullFirst() null 처음

### 페이징

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
    
- 페이징 시 fetch() 사용 시 조회 건수가 제한(count 쿼리 안 나감)
- fetchResults() 사용 시 페이징 수, 총 갯수 등을 확인 가능(but. count 쿼리가 나감)
  - count 쿼리는 조인이 필요 없느 경우도 있는데 이 경우 조인 되어 성능 최적화가 필요
  -> count 전용 쿼리를 별도로 작성!!!
  
### 집합

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
    
- JPQL이 제공하는 모든 집합 함수 제공
- tuple로 반환

### GruopBy

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
    
- 그룹화 하여 쿼리 생성
- 그룹화의 결과를 제한하려면 having 사용

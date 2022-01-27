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
      
      
## Q타입 생성
- gradle -> Tasks -> other -> compileQuerydsl 에서 Q타입 컴파일
- build -> generated -> querydsl -> study.querydsl.entity.Q-- 파일 생성 확인
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      
      

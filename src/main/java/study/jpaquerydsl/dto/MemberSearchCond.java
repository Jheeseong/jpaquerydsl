package study.jpaquerydsl.dto;

import lombok.Data;

@Data
public class MemberSearchCond {

    private String userName;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}

package study.jpaquerydsl.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","name","age"})
public class Member {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int age;
}

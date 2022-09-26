package baedalmate.baedalmate.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Tag {
    @GeneratedValue
    @Id
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tag_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    //== constructor ==//
    private Tag() {}

    private Tag(String name) {
        this.name = name;
    }

    //== 생성 메서드 ==//
    public static Tag createTag(String name) {
        return new Tag(name);
    }

    //== 연관관계 편의 메서드 ==//
    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}

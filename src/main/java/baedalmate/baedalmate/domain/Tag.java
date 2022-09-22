package baedalmate.baedalmate.domain;

import javax.persistence.*;

@Entity
public class Tag {
    @GeneratedValue
    @Id
    @Column(name = "tag_id")
    private Long id;

    @Column(name = "tagname")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Recruit recruit;

    //== 연관관계 편의 메서드 ==//
    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}

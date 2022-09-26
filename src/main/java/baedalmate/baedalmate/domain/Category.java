package baedalmate.baedalmate.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Category {
    @GeneratedValue
    @Id
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Recruit> recruits = new ArrayList<>();

    //== 연관관계 편의 메서드 ==//
    public void addRecruit(Recruit recruit) {
        recruits.add(recruit);
        recruit.setCategory(this);
    }

    //== Constructor ==//
    public Category() {}

    public Category(String name) {
        this.name = name;
    }
}

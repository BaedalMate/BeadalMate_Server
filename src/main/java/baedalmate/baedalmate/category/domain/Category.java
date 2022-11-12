package baedalmate.baedalmate.category.domain;

import baedalmate.baedalmate.recruit.domain.Recruit;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Category {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name")
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Recruit> recruits = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<CategoryImage> images = new ArrayList<>();

    //== 연관관계 편의 메서드 ==//
    public void addRecruit(Recruit recruit) {
        recruits.add(recruit);
        recruit.setCategory(this);
    }

    public void addCategoryImage(CategoryImage categoryImage) {
        images.add(categoryImage);
        categoryImage.setCategory(this);
    }

    //== Constructor ==//
    public Category() {}

    public Category(String name) {
        this.name = name;
    }
}

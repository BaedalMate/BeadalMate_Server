package baedalmate.baedalmate.category.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class CategoryImage {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "category_image_id")
    private Long id;

    @Column(name = "image_name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    //== constructor ==//
    private CategoryImage() {}

    private CategoryImage(String name) {
        this.name = name;
    }

    //== 생성 메서드 ==//
    public static CategoryImage createCategoryImage(Category category, String name) {
        CategoryImage categoryImage = new CategoryImage(name);
        category.addCategoryImage(categoryImage);

        return categoryImage;
    }

    //== 연관관계 편의 메서드 ==//
    public void setCategory(Category category) {
        this.category= category;
    }
}

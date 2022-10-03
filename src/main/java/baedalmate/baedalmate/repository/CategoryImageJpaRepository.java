package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.CategoryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryImageJpaRepository extends JpaRepository<CategoryImage, Long> {

    @Query("select ci from CategoryImage ci where ci.category.id = :id")
    List<CategoryImage> findByCategoryId(@Param("id") Long categoryId);
}

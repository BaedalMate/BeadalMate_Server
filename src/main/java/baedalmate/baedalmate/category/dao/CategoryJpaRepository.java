package baedalmate.baedalmate.category.dao;

import baedalmate.baedalmate.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, Long> {

}

package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class CategoryRepository {

    private final EntityManager em;

    public Category findOne(Long id) {
        return em.find(Category.class, id);
    }

    public Category findByName(String name) {
        return em.createQuery("select c from Category c where c.name = :name", Category.class)
                .setParameter("name", name)
                .getSingleResult();
    }
}

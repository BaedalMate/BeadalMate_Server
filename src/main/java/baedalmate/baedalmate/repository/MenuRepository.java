package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class MenuRepository {
    private final EntityManager em;

    public void save(Menu menu) {
        em.persist(menu);
    }
}

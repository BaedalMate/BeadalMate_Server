package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Recruit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class RecruitRepository {

    private final EntityManager em;

    public void save(Recruit recruit) {
        em.persist(recruit);
    }
}

package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitJpaRepository extends JpaRepository<Recruit, Long> {
    Page<Recruit> findAll(Pageable pageable);
}

package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
}

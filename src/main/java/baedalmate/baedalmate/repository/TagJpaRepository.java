package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {

    @Query("select t from Tag t join fetch t.recruit where t.recruit.id = :id")
    List<Tag> findAllByRecruitIdUsingJoin(@Param("id") Long recruitId);
}

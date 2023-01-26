package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagJpaRepository extends JpaRepository<Tag, Long> {
    @Modifying(clearAutomatically = true)
    @Query("delete from Tag t where t.recruit.id = :recruitId")
    void deleteByRecruitId(@Param("recruitId") Long recruitId);
}

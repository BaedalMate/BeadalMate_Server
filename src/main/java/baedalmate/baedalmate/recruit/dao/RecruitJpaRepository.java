package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.recruit.domain.Recruit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitJpaRepository extends JpaRepository<Recruit, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.view = r.view + 1 where r.id = :id")
    int updateView(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.currentPrice = :price where r.id = :id")
    int updateCurrentPrice(@Param("price") int price, @Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.currentPeople = r.currentPeople + 1 where r.id = :id")
    int updateCurrentPeople(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.currentPeople = r.currentPeople - 1 where r.id = :id")
    int reduceCurrentPeople(@Param("id") Long recruitId);
}

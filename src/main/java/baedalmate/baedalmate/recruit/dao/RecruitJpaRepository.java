package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

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

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false where r.id = :id")
    void setActiveFalse(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.cancel = true where r.id = :id")
    void setCancelTrue(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.cancel = true where r.active = true and r.cancel = false and r.deadlineDate < :date")
    void setCancelTrueFromRecruitExceedTime(@Param("date") LocalDateTime date);
}

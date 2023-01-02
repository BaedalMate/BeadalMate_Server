package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface RecruitJpaRepository extends JpaRepository<Recruit, Long> {

    @Query("select r from Recruit r join fetch r.orders where r.id = :id")
    Recruit findByIdUsingJoinWithOrder(@Param("id") Long id);

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
    @Query("update Recruit r set r.cancel = true, r.active = false where r.id = :id")
    void setCancelTrueAndActiveFalse(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.cancel = true where r.active = true and r.cancel = false and r.criteria <> baedalmate.baedalmate.recruit.domain.Criteria.TIME and r.deadlineDate < :date")
    void setCancelTrueFromRecruitExceedTime(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false where r.active = true and r.cancel = false and r.criteria = baedalmate.baedalmate.recruit.domain.Criteria.TIME and r.deadlineDate < :date")
    void setActiveFalseFromRecruitExceedTime(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.fail = true where r.active = true and r.cancel = false and r.criteria = baedalmate.baedalmate.recruit.domain.Criteria.TIME and r.deadlineDate < :date")
    void setFailTrueAndActiveFalseFromRecruitExceedTime(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.cancel = true where r.active = true and r.user.id = :userId")
    void setCancelTrueByUserId(@Param("userId") Long userId);
}

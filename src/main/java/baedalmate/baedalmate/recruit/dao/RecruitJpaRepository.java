package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.recruit.domain.Recruit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecruitJpaRepository extends JpaRepository<Recruit, Long>, RecruitCustomRepository {

    @Query("select r from Recruit r join fetch r.orders where r.id = :id")
    Recruit findByIdUsingJoinWithOrder(@Param("id") Long id);

    @Query("select r from Recruit r join fetch r.user where r.user.id = :userId")
    List<Recruit> findAllActivateByUserIdUsingJoin(@Param("userId") Long userId);

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
    @Query("update Recruit r set r.active = false, r.deactivateDate = :date where r.id = :id")
    void setActiveFalse(@Param("id") Long recruitId, @Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.cancel = true, r.active = false, r.deactivateDate = :date where r.id = :id")
    void setCancelTrueAndActiveFalse(@Param("id") Long recruitId, @Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.deactivateDate = :date  " +
            "where r.active = true and r.cancel = false and r.fail = false " +
            "and r.criteria = baedalmate.baedalmate.recruit.domain.Criteria.TIME " +
            "and r.currentPeople != 1" +
            "and r.deadlineDate < :date")
    void setActiveFalseFromRecruitExceedTime(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.fail = true, r.deactivateDate = :date  " +
            "where r.active = true and r.cancel = false and r.fail = false " +
            "and (r.criteria != baedalmate.baedalmate.recruit.domain.Criteria.TIME or (r.criteria = baedalmate.baedalmate.recruit.domain.Criteria.TIME and r.currentPeople = 1)) " +
            "and r.deadlineDate < :date")
    void setFailTrueAndActiveFalseFromRecruitExceedTime(@Param("date") LocalDateTime date);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.active = false, r.cancel = true where r.active = true and r.user.id = :userId")
    void setCancelTrueByUserId(@Param("userId") Long userId);

    @Query("select distinct r from Recruit r " +
            "join r.orders " +
            "where r.active = true and r.cancel = false " +
            "and r.criteria = baedalmate.baedalmate.recruit.domain.Criteria.TIME " +
            "and r.currentPeople != 1" +
            "and r.deadlineDate < :date")
    List<Recruit> findAllByDeadlineDateAndCriteriaDate(@Param("date") LocalDateTime date);

    @Query("select distinct r from Recruit r " +
            "where r.active = true and r.cancel = false " +
            "and (r.criteria != baedalmate.baedalmate.recruit.domain.Criteria.TIME or (r.criteria = baedalmate.baedalmate.recruit.domain.Criteria.TIME and r.currentPeople = 1)) " +
            "and r.deadlineDate < :date")
    List<Recruit> findAllByDeadlineDateAndCriteriaNotDate(@Param("date") LocalDateTime date);
}

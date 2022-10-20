package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Dormitory;
import baedalmate.baedalmate.domain.Recruit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecruitJpaRepository extends JpaRepository<Recruit, Long> {

    @Query("select r from Recruit r join fetch r.user join fetch r.shippingFees where r.active = true order by r.user.score DESC")
    List<Recruit> findAllUsingJoinOrderByScore(Pageable pageable);

    @Query("select r from Recruit r join fetch r.user join fetch r.shippingFees where r.active = true order by r.deadlineDate ASC")
    List<Recruit> findAllUsingJoinOrderByDeadlineDate(Pageable pageable);

    @Query("select r from Recruit r join fetch r.user join fetch r.shippingFees where r.active = true order by r.view DESC")
    List<Recruit> findAllUsingJoinOrderByView(Pageable pageable);

    @Query("select r from Recruit r join fetch r.user join fetch r.shippingFees where r.category.id = :id and r.active = true order by r.user.score DESC")
    List<Recruit> findAllByCategoryUsingJoinOrderByScore(@Param("id") Long categoryId, Pageable pageable);

    @Query("select r from Recruit r join fetch r.user join fetch r.shippingFees where r.category.id = :id and r.active = true order by r.deadlineDate ASC")
    List<Recruit> findAllByCategoryUsingJoinOrderByDeadlineDate(@Param("id") Long categoryId, Pageable pageable);

    @Query("select r from Recruit r join fetch r.user join fetch r.shippingFees where r.category.id = :id and r.active = true order by r.view DESC")
    List<Recruit> findAllByCategoryUsingJoinOrderByView(@Param("id") Long categoryId, Pageable pageable);

    @Query("select r from Recruit r join fetch r.user where r.dormitory = :dormitory and r.active = true order by r.deadlineDate ASC")
    List<Recruit> findAllUsingJoinOrderByDeadlineDate(@Param("dormitory") Dormitory dormitory, Pageable pageable);

    @Query("select r from Recruit r join fetch r.user join fetch r.chatRoom where r.id = :id")
    Recruit findByIdUsingJoin(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.view = r.view + 1 where r.id = :id")
    int updateView(@Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.currentPrice = :price where r.id = :id")
    int updateCurrentPrice(@Param("price") int price, @Param("id") Long recruitId);

    @Modifying(clearAutomatically = true)
    @Query("update Recruit r set r.currentPeople = r.currentPeople + 1 where r.id = :id")
    int updateCurrentPeople(@Param("id") Long recruitId);

}

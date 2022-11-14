package baedalmate.baedalmate.order.dao;

import baedalmate.baedalmate.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.recruit join fetch o.menus join fetch o.user where o.recruit.id = :id")
    List<Order> findAllByRecruitIdUsingJoin(@Param("id") Long recruitId);

    @Query("select o from Order o join fetch o.recruit join fetch o.menus join fetch o.user where o.recruit.id = :recruitId and o.user.id = :userId")
    Order findByUserIdAndRecruitIdUsingJoin(@Param("userId") Long userId, @Param("recruitId") Long recruitId);

}

package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Order;
import baedalmate.baedalmate.domain.Recruit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.recruit join fetch o.menus join fetch o.user where o.recruit = :recruit")
    List<Order> findAllByRecruit(@Param("recruit") Recruit recruit);
}

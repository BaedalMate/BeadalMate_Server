package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.order.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuJpaRepository extends JpaRepository<Menu, Long> {
    @Modifying(clearAutomatically = true)
    @Query("delete from Menu m where m in " +
            "(select m from Order o join o.menus m where o.id = :orderId)")
    void deleteByOrderId(@Param("orderId") Long orderId);
}

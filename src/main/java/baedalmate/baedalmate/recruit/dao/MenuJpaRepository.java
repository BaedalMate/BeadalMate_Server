package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.order.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuJpaRepository extends JpaRepository<Menu, Long> {
}

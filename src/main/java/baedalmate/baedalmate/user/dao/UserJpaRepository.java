package baedalmate.baedalmate.user.dao;

import baedalmate.baedalmate.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
}

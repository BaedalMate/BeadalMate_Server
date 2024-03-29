package baedalmate.baedalmate.user.dao;

import baedalmate.baedalmate.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    @Modifying(clearAutomatically = true)
    @Query("update User u set u.score = :score where u.id = :id")
    void updateScore(@Param("score") float score, @Param("id") Long userId);

    @Query("select u from User u join fetch u.recruits where u.id = :id")
    User findByIdUsingJoinWithRecruit(@Param("id") Long id);

    @Query("select u from User u left join u.blocks where u.id = :id")
    User findByIdUsingJoinWithBlock(@Param("id") Long id);
}

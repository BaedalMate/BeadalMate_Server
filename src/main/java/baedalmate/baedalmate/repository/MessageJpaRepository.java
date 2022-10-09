package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageJpaRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m join fetch m.chatRoom join fetch m.user where m.user.id = :userId")
    List<Message> findAllByUserIdUsingJoin(@Param("userId") Long userId);
}

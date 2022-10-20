package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    @Query("select cr from ChatRoom cr join fetch cr.recruit join fetch cr.messages where cr.id = :id")
    ChatRoom findOne(@Param("id") Long id);

    @Query("select cr from ChatRoom cr join fetch cr.recruit join fetch cr.messages where cr.recruit.id = :id")
    ChatRoom findByRecruitId(@Param("id") Long recruitId);
}

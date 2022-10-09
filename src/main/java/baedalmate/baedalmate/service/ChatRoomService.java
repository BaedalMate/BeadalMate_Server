package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.ChatRoom;
import baedalmate.baedalmate.repository.ChatRoomJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomJpaRepository chatRoomJpaRepository;

    @Transactional
    public Long save(ChatRoom chatRoom) {
        chatRoomJpaRepository.save(chatRoom);
        return chatRoom.getId();
    }

    public ChatRoom findById(Long id) {
        return chatRoomJpaRepository.findOne(id);
    }
}

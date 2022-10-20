package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.repository.ChatRoomJpaRepository;
import baedalmate.baedalmate.repository.MessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MessageJpaRepository messageJpaRepository;

    @Transactional
    public Long save(User user, ChatRoom chatRoom) {
        chatRoomJpaRepository.save(chatRoom);
        // message 생성
        Message message = Message.createMessage(MessageType.ENTER, "", user, chatRoom);
        messageJpaRepository.save(message);
        return chatRoom.getId();
    }

    public ChatRoom findOne(Long id) {
        return chatRoomJpaRepository.findOne(id);
    }

    public ChatRoom findByRecruitId(Long recruitId) {
        return chatRoomJpaRepository.findByRecruitId(recruitId);
    }

}

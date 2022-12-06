package baedalmate.baedalmate.chat.service;

import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.chat.dao.ChatRoomJpaRepository;
import baedalmate.baedalmate.chat.dao.MessageJpaRepository;
import baedalmate.baedalmate.chat.dto.*;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final RecruitJpaRepository recruitJpaRepository;
    private final UserJpaRepository userJpaRepository;
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

    public ChatRoomDetailDto getChatRoomDetail(Long id) {
        ChatRoom chatRoom = chatRoomJpaRepository.findById(id).get();
        List<MessageDto> messageInfos = chatRoom.getMessages().stream()
                .map(m -> new MessageDto(m.getId(), m.getUser().getId(), m.getUser().getNickname(), m.getUser().getProfileImage(), m.getMessage(), m.getCreateDate()))
                .collect(Collectors.toList());
        Recruit recruit = chatRoom.getRecruit();
        ChatRoomRecruitDetailDto recruitDetail = new ChatRoomRecruitDetailDto(
                recruit.getId(),
                recruit.getImage(),
                recruit.getCreateDate(),
                recruit.getTitle(),
                recruit.getCriteria(),
                recruit.getMinPrice(),
                recruit.getMinPeople(),
                recruit.getDeadlineDate(),
                recruit.isActive(),
                recruit.isCancel()
        );
        return new ChatRoomDetailDto(chatRoom.getId(), recruitDetail, messageInfos);
    }

    public ChatRoomListDto getChatRoomList(Long userId) {
        User user = userJpaRepository.findById(userId).get();

        List<ChatRoom> chatRooms = messageJpaRepository.findAllByUserIdUsingJoin(user.getId())
                .stream().map(m -> m.getChatRoom()).collect(Collectors.toList());
        List<ChatRoomDto> chatRoomInfos = chatRooms.stream().distinct().map(
                c -> {
                    Message message = c.getMessages().get(c.getMessages().size() - 1);
                    MessageDto messageInfo = new MessageDto(message.getId(), message.getUser().getId(), message.getUser().getNickname(), message.getUser().getProfileImage(), message.getMessage(), message.getCreateDate());
                    return new ChatRoomDto(c.getId(), c.getRecruit().getImage(), c.getRecruit().getTitle(), messageInfo);
                }
        ).collect(Collectors.toList());
        Collections.sort(chatRoomInfos);
        return new ChatRoomListDto(chatRoomInfos);
    }
}

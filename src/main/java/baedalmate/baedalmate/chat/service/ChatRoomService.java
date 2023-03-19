package baedalmate.baedalmate.chat.service;

import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.domain.MessageType;
import baedalmate.baedalmate.chat.dao.ChatRoomJpaRepository;
import baedalmate.baedalmate.chat.dao.MessageJpaRepository;
import baedalmate.baedalmate.chat.dto.*;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.review.dao.ReviewJpaRepository;
import baedalmate.baedalmate.review.domain.Review;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final OrderJpaRepository orderJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final ChatRoomJpaRepository chatRoomJpaRepository;
    private final MessageJpaRepository messageJpaRepository;
    private final ReviewJpaRepository reviewJpaRepository;

    @Transactional
    public Long save(User user, ChatRoom chatRoom) {
        chatRoomJpaRepository.save(chatRoom);
        // message 생성
        Message message = Message.createMessage(MessageType.ENTER, "", user, chatRoom, null);
        messageJpaRepository.save(message);
        return chatRoom.getId();
    }

    public ChatRoom findOne(Long id) {
        return chatRoomJpaRepository.findOne(id);
    }

    public ChatRoom findByRecruitId(Long recruitId) {
        return chatRoomJpaRepository.findByRecruitId(recruitId);
    }

    public ChatRoomDetailDto getChatRoomDetail(Long userId, Long id) {
        ChatRoom chatRoom = chatRoomJpaRepository.findById(id).get();
        Recruit recruit = chatRoom.getRecruit();
        List<Order> orders = orderJpaRepository.findAllByRecruitId(recruit.getId());
        List<Long> userIdList = orders.stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
        List<MessageDto> messagesDto = new ArrayList<>();
        List<Message> messages = chatRoom.getMessages();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message m = messages.get(i);
            if(MessageType.TALK.equals(m.getMessageType())) {
                MessageDto messageDto = new MessageDto(m.getId(), m.getUser().getId(), m.getUser().getNickname(), m.getUser().getProfileImage(), m.getMessage(), m.getCreateDate(), m.getMessageType(), m.getReadMessageId(), userIdList.size());
                messagesDto.add(messageDto);
            }
            if(MessageType.READ.equals(m.getMessageType())) {
                userIdList.remove(messages.get(i).getUser().getId());
                MessageDto messageDto = new MessageDto(m.getId(), m.getUser().getId(), m.getUser().getNickname(), m.getUser().getProfileImage(), m.getMessage(), m.getCreateDate(), m.getMessageType(),  m.getReadMessageId(), null);
//                messagesDto.add(messageDto);
            }
            if(MessageType.ENTER.equals(m.getMessageType())) {
                MessageDto messageDto = new MessageDto(m.getId(), m.getUser().getId(), m.getUser().getNickname(), m.getUser().getProfileImage(), m.getMessage(), m.getCreateDate(), m.getMessageType(), m.getReadMessageId(), null);
//                messagesDto.add(messageDto);
            }
        }
        Collections.reverse(messagesDto);
        List<Review> reviews = reviewJpaRepository.findAllByRecruitIdUsingJoin(recruit.getId());
        boolean reviewed = reviews.stream().anyMatch(r -> r.getUser().getId() == userId);
        ChatRoomRecruitDetailDto recruitDetail = new ChatRoomRecruitDetailDto(
                recruit.getId(),
                recruit.getImage(),
                recruit.getCreateDate(),
                recruit.getTitle(),
                recruit.getCriteria(),
                recruit.getMinPrice(),
                recruit.getMinPeople(),
                recruit.getDeadlineDate(),
                recruit.getDeactivateDate(),
                recruit.isActive(),
                recruit.isCancel(),
                recruit.isFail()
        );
        return new ChatRoomDetailDto(chatRoom.getId(), recruitDetail, messagesDto, reviewed, recruit.getCurrentPeople());
    }

    public ChatRoomListDto getChatRoomList(Long userId) {
        List<Order> orders = orderJpaRepository.findAllByUserId(userId);
        List<ChatRoom> chatRooms = orders.stream().map(o -> o.getRecruit().getChatRoom())
                .collect(Collectors.toList());
        List<ChatRoomDto> chatRoomInfos = chatRooms.stream().map(
                c -> {
                    Message message = null;
                    int count = 0;
                    boolean flagCountUnreadMessages = false;
                    Collections.reverse(c.getMessages());
                    for (Message m : c.getMessages()) {
                        if (MessageType.TALK.equals(m.getMessageType())) {
                            if (message == null) {
                                message = m;
                            }
                            if (!flagCountUnreadMessages) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        if (MessageType.READ.equals(m.getMessageType()) && m.getUser().getId() == userId) {
                            flagCountUnreadMessages = true;
                        }
                    }
                    if (message == null) {
                        message = c.getMessages().get(0);
                    }
                    MessageDtoForChatRoom messageInfo = new MessageDtoForChatRoom(message.getId(), message.getUser().getId(), message.getUser().getNickname(), message.getUser().getProfileImage(), message.getMessage(), message.getCreateDate(), message.getMessageType());
                    return new ChatRoomDto(c.getId(), c.getRecruit().getImage(), c.getRecruit().getTitle(), messageInfo, count);
                }
        ).collect(Collectors.toList());
        Collections.sort(chatRoomInfos);
        return new ChatRoomListDto(chatRoomInfos);
    }

    public UnreadUsersDto getUnreadUser(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomJpaRepository.findById(chatRoomId).get();
        Recruit recruit = chatRoom.getRecruit();
        List<Order> orders = orderJpaRepository.findAllByRecruitId(recruit.getId());
        List<Long> userIdList = orders.stream().map(o -> o.getUser().getId()).collect(Collectors.toList());
        List<UnreadUserAndMessageIdDto> unreadUserAndMessageIdDtos = new ArrayList<>();
        List<Message> messages = chatRoom.getMessages();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message m = messages.get(i);
            if(MessageType.TALK.equals(m.getMessageType())) {
                UnreadUserAndMessageIdDto unreadUserAndMessageIdDto = new UnreadUserAndMessageIdDto(userIdList.size(), m.getId());
                unreadUserAndMessageIdDtos.add(unreadUserAndMessageIdDto);
            }
            if(MessageType.READ.equals(m.getMessageType())) {
                userIdList.remove(messages.get(i).getUser().getId());
            }
        }
        Collections.reverse(unreadUserAndMessageIdDtos);
        return new UnreadUsersDto(unreadUserAndMessageIdDtos);
    }
}

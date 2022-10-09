package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.service.ChatRoomService;
import baedalmate.baedalmate.service.MessageService;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    public ChatRoomList getRooms(
            @CurrentUser PrincipalDetails principalDetails
    ) {

        // 유저 조회
        User user = principalDetails.getUser();

        List<ChatRoom> chatRooms = messageService.findAllByUserId(user.getId())
                .stream().map(m -> m.getChatRoom()).collect(Collectors.toList());

        List<ChatRoomInfo> chatRoomInfos = chatRooms.stream().distinct().map(
                c -> {
                    Message message = c.getMessages().get(c.getMessages().size()-1);
                    MessageInfo messageInfo = new MessageInfo(message.getId(), message.getUser().getNickname(), message.getMessage(), message.getCreateDate());
                    return new ChatRoomInfo(c.getId(), messageInfo);
                }
        ).collect(Collectors.toList());
        Collections.sort(chatRoomInfos);

        return new ChatRoomList(chatRoomInfos);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    public ChatRoomDetail getMessages(@PathVariable Long roomId) {
        ChatRoom chatRoom = chatRoomService.findById(roomId);
        List<MessageInfo> messageInfos = chatRoom.getMessages().stream()
                .map(m -> new MessageInfo(m.getId(), m.getUser().getNickname(), m.getMessage(), m.getCreateDate()))
                .collect(Collectors.toList());
        Recruit recruit = chatRoom.getRecruit();
        RecruitInfo recruitInfo = new RecruitInfo(
                recruit.getId(),
                recruit.getCreateDate(),
                recruit.getTitle(),
                recruit.getCriteria(),
                recruit.getMinPrice(),
                recruit.getMinPeople(),
                recruit.getDeadlineDate(),
                recruit.isActive()
        );

        return new ChatRoomDetail(chatRoom.getId(), recruitInfo, messageInfos);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChatRoomList {
        List<ChatRoomInfo> rooms;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChatRoomInfo implements Comparable<ChatRoomInfo> {
        private Long id;
        private MessageInfo lastMessage;

        @Override
        public int compareTo(ChatRoomInfo o) {
            return o.getLastMessage().getSendDate().isAfter(getLastMessage().getSendDate()) ? 1 : -1;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ChatRoomDetail {
        private Long id;
        private RecruitInfo recruit;
        private List<MessageInfo> messages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MessageInfo {
        private Long id;
        private String sender;
        private String message;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime sendDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class RecruitInfo {
        private Long recruitId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDate;
        private String title;
        private Criteria criteria;
        private int minPrice;
        private int minPeople;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;
        private boolean active;
    }
}

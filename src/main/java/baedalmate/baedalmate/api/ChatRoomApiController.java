package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.service.ChatRoomService;
import baedalmate.baedalmate.service.MessageService;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"채팅방 조회 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomApiController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    // 모든 채팅방 목록 반환
    @ApiOperation(value = "채팅방 전체 조회")
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
                    return new ChatRoomInfo(c.getId(), c.getRecruit().getImage(), messageInfo);
                }
        ).collect(Collectors.toList());
        Collections.sort(chatRoomInfos);

        return new ChatRoomList(chatRoomInfos);
    }

    // 특정 채팅방 조회
    @ApiOperation(value = "특정 채팅방 조회")
    @GetMapping("/room/{roomId}")
    public ChatRoomDetail getMessages(@PathVariable Long roomId) {
        ChatRoom chatRoom = chatRoomService.findOne(roomId);
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
    @Schema
    static class ChatRoomList {
        @Schema(description = "채팅방 리스트")
        List<ChatRoomInfo> rooms;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    static class ChatRoomInfo implements Comparable<ChatRoomInfo> {

        @Schema(description = "채팅방 id")
        private Long id;
        @Schema(description = "모집글 이미지")
        private String image;
        @Schema(description = "채팅방 최근 메세지")
        private MessageInfo lastMessage;

        @Override
        public int compareTo(ChatRoomInfo o) {
            return o.getLastMessage().getSendDate().isAfter(getLastMessage().getSendDate()) ? 1 : -1;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema
    static class ChatRoomDetail {
        @Schema(description = "채팅방 id")
        private Long id;
        @Schema(description = "모집글 정보")
        private RecruitInfo recruit;
        @Schema(description = "메세지 리스트")
        private List<MessageInfo> messages;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MessageInfo {
        @Schema(description = "메세지 id")
        private Long id;
        @Schema(description = "보낸 사람 닉네임")
        private String sender;
        @Schema(description = "메세지 내용")
        private String message;
        @Schema(description = "보낸 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime sendDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class RecruitInfo {
        @Schema(description = "모집글 id")
        private Long recruitId;
        @Schema(description = "모집글 생성 시간")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime createDate;
        @Schema(description = "모집글 제목")
        private String title;
        @Schema(description = "모집글 마감기준")
        private Criteria criteria;
        @Schema(description = "모집글 최소주문금액")
        private int minPrice;
        @Schema(description = "모집글 최소인원")
        private int minPeople;
        @Schema(description = "모집글 마감날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime deadlineDate;
        @Schema(description = "모집글 활성화 여부")
        private boolean active;
    }
}

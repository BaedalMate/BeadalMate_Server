package baedalmate.baedalmate.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class ChatRoomDto implements Comparable<ChatRoomDto> {

    @Schema(description = "채팅방 id")
    private Long chatRoomId;
    @Schema(description = "모집글 이미지")
    private String image;
    @Schema(description = "모집글 제목")
    private String title;
    @Schema(description = "채팅방 최근 메세지")
    private MessageDtoForChatRoom lastMessage;
    @Schema(description = "안읽은 메세지 수")
    private Integer numberUnreadMessages;

    @Override
    public int compareTo(ChatRoomDto o) {
        return o.getLastMessage().getSendDate().isAfter(getLastMessage().getSendDate()) ? 1 : -1;
    }
}
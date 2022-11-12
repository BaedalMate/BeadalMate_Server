package baedalmate.baedalmate.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class ChatRoomListDto {
    @Schema(description = "채팅방 리스트")
    List<ChatRoomDto> rooms;
}

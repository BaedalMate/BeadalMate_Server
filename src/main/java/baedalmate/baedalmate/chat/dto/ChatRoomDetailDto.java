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
public class ChatRoomDetailDto {
    @Schema(description = "채팅방 id")
    private Long id;
    @Schema(description = "모집글 정보")
    private RecruitDetailDto recruit;
    @Schema(description = "메세지 리스트")
    private List<MessageDto> messages;
}
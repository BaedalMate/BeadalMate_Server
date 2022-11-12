package baedalmate.baedalmate.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class OrderAndChatIdDto {
    @Schema(description = "주문 id", example = "1")
    private Long id;
    @Schema(description = "채팅방 id", example = "1")
    private Long chatRoomId;
}

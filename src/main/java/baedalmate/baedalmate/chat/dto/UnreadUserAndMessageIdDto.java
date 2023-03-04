package baedalmate.baedalmate.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class UnreadUserAndMessageIdDto {
    @Schema(description = "메시지를 읽지 않은 사람의 수")
    private int unreadUser;
    @Schema(description = "메세지 id")
    private Long messageId;
}

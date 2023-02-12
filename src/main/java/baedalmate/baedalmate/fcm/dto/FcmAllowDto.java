package baedalmate.baedalmate.fcm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema
public class FcmAllowDto {
    @Schema(description = "채팅 알림 허용", example = "true")
    private boolean allowChat;
    @Schema(description = "모집글 알림 허용", example = "true")
    private boolean allowRecruit;
}

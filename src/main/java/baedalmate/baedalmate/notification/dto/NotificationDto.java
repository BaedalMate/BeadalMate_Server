package baedalmate.baedalmate.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class NotificationDto {
    @Schema(description = "모집글 타이틀", example = "치킨 같이 먹어요~")
    private String title;
    @Schema(description = "알림 내용", example = "모집이 마감되었습니다.")
    private String body;
    @Schema(description = "이미지 명", example = "12345.jpg")
    private String image;
    @Schema(description = "채팅방 id", example = "2")
    private Long chatRoomId;
}

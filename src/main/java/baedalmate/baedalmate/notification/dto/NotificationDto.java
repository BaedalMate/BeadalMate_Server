package baedalmate.baedalmate.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @Schema(description = "알림 생성 시간", example = "2020-12-24 16:28:27")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
}

package baedalmate.baedalmate.chat.dto;

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
public class MessageDto {
    @Schema(description = "메세지 id")
    private Long id;
    @Schema(description = "보낸 사람 id")
    private Long senderId;
    @Schema(description = "보낸 사람 닉네임")
    private String sender;
    @Schema(description = "보낸 사람 프로필 이미지")
    private String senderImage;
    @Schema(description = "메세지 내용")
    private String message;
    @Schema(description = "보낸 시간")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sendDate;
}
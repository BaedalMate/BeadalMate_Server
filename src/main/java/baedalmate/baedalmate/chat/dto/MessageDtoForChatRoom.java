package baedalmate.baedalmate.chat.dto;

import baedalmate.baedalmate.chat.domain.MessageType;
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
public class MessageDtoForChatRoom {
    @Schema(description = "메세지 id", example = "1")
    private Long messageId;
    @Schema(description = "보낸 사람 id", example = "1")
    private Long senderId;
    @Schema(description = "보낸 사람 닉네임", example = "허동준")
    private String sender;
    @Schema(description = "보낸 사람 프로필 이미지", example = "123456.jpg")
    private String senderImage;
    @Schema(description = "메세지 내용", example = "ㅎㅇㅎㅇ")
    private String message;
    @Schema(description = "보낸 시간", example = "2022-12-25 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime sendDate;
    @Schema(description = "타입", example = "TALK")
    private MessageType type;
}
package baedalmate.baedalmate.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class MessageDto {
    //채팅방 ID
    @Schema(description = "채팅방 id")
    @NotNull
    private Long roomId;
    //보내는 사람
    @Schema(description = "보낸사람 id")
    @NotNull
    private Long senderId;
    @Schema(description = "보낸사람 닉네임 (보낼 때 포함X)")
    private String sender;
    @Schema(description = "보낸사람 프로필이미지 (보낼 때 포함X)")
    private String senderImage;
    //내용
    @Schema(description = "메세지 내용")
    @NotNull
    private String message;
}
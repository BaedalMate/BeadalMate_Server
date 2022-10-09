package baedalmate.baedalmate.dto;

import baedalmate.baedalmate.domain.MessageType;
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

    @Schema(description = "메세지 타입 (ENTER | TALK")
    @NotNull
    private MessageType type;
    //채팅방 ID
    @Schema(description = "채팅방 id")
    @NotNull
    private Long roomId;
    //보내는 사람
    @Schema(description = "보낸사람 id")
    @NotNull
    private Long senderId;
    @Schema(description = "보낸사람 닉네임 (보낼 때 포함안하셔도 됩니다)")
    private String senderNickName;
    //내용
    @Schema(description = "메세지 내용")
    @NotNull
    private String message;
}
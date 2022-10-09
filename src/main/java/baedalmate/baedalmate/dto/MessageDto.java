package baedalmate.baedalmate.dto;

import baedalmate.baedalmate.domain.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {

    @NotNull
    private MessageType type;
    //채팅방 ID
    @NotNull
    private Long roomId;
    //보내는 사람
    @NotNull
    private Long senderId;
    private String senderNickName;
    //내용
    @NotNull
    private String message;
}
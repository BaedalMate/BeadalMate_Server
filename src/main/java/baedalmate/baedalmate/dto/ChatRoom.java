package baedalmate.baedalmate.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    private Long roomId;
    private String roomName;

    public static ChatRoom create(Long roomId, String name) {
        ChatRoom room = new ChatRoom();
        room.roomId = roomId;
        room.roomName = name;
        return room;
    }
}
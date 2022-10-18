package baedalmate.baedalmate.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class ChatRoom {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "chat_room_id")
    private Long id;

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    //== constructor ==//
    private ChatRoom() {}

    //== 생성 메서드 ==//
    public static ChatRoom createChatRoom(Recruit recruit) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.recruit = recruit;
        recruit.setChatRoom(chatRoom);
        return chatRoom;
    }

    //== 연관관계 편의 메서드 ==//
    public void addMessage(Message message) {
        messages.add(message);
        message.setChatRoom(this);
    }
}
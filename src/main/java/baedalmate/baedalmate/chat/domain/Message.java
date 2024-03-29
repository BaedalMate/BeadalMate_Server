package baedalmate.baedalmate.chat.domain;

import baedalmate.baedalmate.user.domain.User;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Message {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "message_id")
    private Long id;

    private MessageType messageType;

    private String message;

    private Long readMessageId;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private LocalDateTime createDate;

    //== constructor ==//
    private Message() {
    }

    private Message(MessageType messageType, String message, Long readMessageId) {
        this.messageType = messageType;
        this.message = message;
        this.readMessageId = readMessageId;
    }

    //== 생성 메서드 ==//
    public static Message createMessage(MessageType messageType, String messageContent, User user, ChatRoom chatRoom, Long readMessageId) {
        Message message = new Message(messageType, messageContent, readMessageId);

        message.setUser(user);
        chatRoom.addMessage(message);

        return message;
    }

    //== setter ==//
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

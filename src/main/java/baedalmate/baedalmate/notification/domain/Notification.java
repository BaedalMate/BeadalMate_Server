package baedalmate.baedalmate.notification.domain;

import baedalmate.baedalmate.user.domain.User;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Notification {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String body;

    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Long chatRoomId;

    @CreationTimestamp
    private LocalDateTime createDate;

    //== constructor ==//
    private Notification() {}

    private Notification(String title, String body, String image, Long chatRoomId) {
        this.title = title;
        this.body = body;
        this.image = image;
        this.chatRoomId = chatRoomId;
    }

    // == 생성 메서드 ==//
    public static Notification createNotification(String title, String body, String image, Long chatRoomId, User user) {
        Notification notification = new Notification(title, body, image, chatRoomId);
        user.addNotification(notification);
        return notification;
    }

    //== setter ==//
    public void setUser(User user) {
        this.user = user;
    }
}

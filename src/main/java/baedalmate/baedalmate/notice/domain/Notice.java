package baedalmate.baedalmate.notice.domain;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Notice {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "notice_id")
    private Long id;

    @CreationTimestamp
    private LocalDateTime createDate;

    private String title;
    private String description;

    //== constructor ==//
    private Notice() {}

    private Notice(String title, String description) {
        this.title = title;
        this.description = description;
    }

    //== 생성 메서드 ==//
    public static Notice createNotice(String title, String description) {
        return new Notice(title, description);
    }
}

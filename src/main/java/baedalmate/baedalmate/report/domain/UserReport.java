package baedalmate.baedalmate.report.domain;

import baedalmate.baedalmate.user.domain.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class UserReport {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target;

    private String reason;
    private String detail;

    //== constructor ==//
    private UserReport() {
    }

    private UserReport(User user, User target, String reason, String detail) {
        this.user = user;
        this.target = target;
        this.reason = reason;
        this.detail = detail;
    }

    //== 생성 메서드 ==//
    public static UserReport createUserReport(User user, User target, String reason, String detail) {
        UserReport userReport = new UserReport(user, target, reason, detail);
        return userReport;
    }
}
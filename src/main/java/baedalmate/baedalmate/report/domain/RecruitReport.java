package baedalmate.baedalmate.report.domain;

import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.user.domain.User;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class RecruitReport {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "recruit_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private Recruit target;

    private String reason;
    private String detail;

    //== constructor ==//
    private RecruitReport() {
    }

    private RecruitReport(User user, Recruit target, String reason, String detail) {
        this.user = user;
        this.target = target;
        this.reason = reason;
        this.detail = detail;
    }

    //== 생성 메서드 ==//
    public static RecruitReport createRecruitReport(User user, Recruit target, String reason, String detail) {
        RecruitReport recruitReport = new RecruitReport(user, target, reason, detail);
        return recruitReport;
    }
}

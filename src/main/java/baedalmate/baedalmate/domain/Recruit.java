package baedalmate.baedalmate.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
public class Recruit {

    @GeneratedValue
    @Id
    @Column(name = "recruit_id")
    private Long id;

    private String restaurant;

    private Dormitory dormitory;

    private Criteria criteria;

    private Platform platform;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int currentPeople;
    private int minPeople;

    private int minPrice;

    private int coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Timestamp createDate;

    private LocalDateTime deadlineDate;

    //== constructor ==//
    private Recruit() {
    }
    private Recruit(int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria,
                    Dormitory dormitory,
                    String restaurant, Platform platform, int coupon) {
        this.minPeople = minPeople;
        this.minPrice = minPrice;
        this.deadlineDate = deadlineDate;
        this.criteria = criteria;
        this.dormitory = dormitory;
        this.restaurant = restaurant;
        this.platform = platform;
        this.coupon = coupon;
    }

    //== 생성 메서드 ==//
    public static Recruit createRecruit(
            User user,
            int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
            String restaurant, Platform platform, int coupon) {
        Recruit recruit = new Recruit(minPeople, minPrice, deadlineDate, criteria, dormitory, restaurant, platform, coupon);
        recruit.setUser(user);
        return recruit;
    }

    //== 연관관계 편의 메서드 ==//
    public void setUser(User user) {
        this.user = user;
        user.addRecruit(this);
    }
}

package baedalmate.baedalmate.domain;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    private int deliveryFee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "recruit")
    private List<Order> orders;

    @CreationTimestamp
    private LocalDateTime createDate;

    private LocalDateTime deadlineDate;

    private String title;

    private String description;



    //== constructor ==//
    private Recruit() {
    }
    private Recruit(int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
                    String restaurant, Platform platform, int coupon, int deliveryFee, String title, String description) {
        this.minPeople = minPeople;
        this.minPrice = minPrice;
        this.deadlineDate = deadlineDate;
        this.criteria = criteria;
        this.dormitory = dormitory;
        this.restaurant = restaurant;
        this.platform = platform;
        this.coupon = coupon;
        this.deliveryFee = deliveryFee;
        this.title = title;
        this.description = description;
    }

    //== 생성 메서드 ==//
    public static Recruit createRecruit(
            User user,
            int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
            String restaurant, Platform platform, int coupon, int deliveryFee, String title, String description) {
        Recruit recruit = new Recruit(minPeople, minPrice, deadlineDate, criteria, dormitory, restaurant, platform, coupon, deliveryFee, title, description);
        recruit.setUser(user);
        return recruit;
    }

    //== 연관관계 편의 메서드 ==//
    public void setUser(User user) {
        this.user = user;
    }

    public void addOrder(Order order) {
        orders.add(order);
        order.setRecruit(this);
    }
}

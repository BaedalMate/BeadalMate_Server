package baedalmate.baedalmate.domain;

import baedalmate.baedalmate.domain.embed.Place;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Recruit {

    @GeneratedValue
    @Id
    @Column(name = "recruit_id")
    private Long id;

    @Embedded
    private Place place;

    private Dormitory dormitory;

    private Criteria criteria;

    private Platform platform;

    @Column(columnDefinition = "integer default 1", nullable = false)
    private int currentPeople;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int views;

    private int minPeople;

    private int minPrice;

    private int coupon;

    private boolean freeShipping;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruit")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruit")
    private List<ShippingFee> shippingFees = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruit")
    private List<Tag> tags = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createDate;

    private LocalDateTime deadlineDate;

    private String title;

    private String description;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean active;

    //== constructor ==//
    private Recruit() {
    }

    private Recruit(int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
                    Place place, Platform platform, int coupon, String title, String description, boolean freeShipping) {
        this.minPeople = minPeople;
        this.minPrice = minPrice;
        this.deadlineDate = deadlineDate;
        this.criteria = criteria;
        this.dormitory = dormitory;
        this.place = place;
        this.platform = platform;
        this.coupon = coupon;
        this.title = title;
        this.description = description;
        this.freeShipping = freeShipping;
    }

    //== 생성 메서드 ==//
    public static Recruit createRecruit(
            User user,
            int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
            Place place, Platform platform, int coupon, String title, String description, boolean freeShipping,
            List<ShippingFee> shippingFees, Order order, List<Tag> tags) {
        Recruit recruit = new Recruit(minPeople, minPrice, deadlineDate, criteria, dormitory, place, platform, coupon, title, description, freeShipping);
        recruit.setUser(user);
        for(ShippingFee shippingFee : shippingFees) {
            recruit.addShippingFee(shippingFee);
        }
        recruit.addOrder(order);
        for(Tag tag : tags) {
            recruit.addTag(tag);
        }
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

    public void addShippingFee(ShippingFee shippingFee) {
        shippingFees.add(shippingFee);
        shippingFee.setRecruit(this);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
        tag.setRecruit(this);
    }

    //== Getter ==//
    public int getMinShippingFee() {
        if(freeShipping) return 0;

        int min = shippingFees.get(0).getShippingFee();
        for(ShippingFee shippingFee : shippingFees) {
            min = Math.min(shippingFee.getShippingFee(), min);
        }
        return min;
    }
}

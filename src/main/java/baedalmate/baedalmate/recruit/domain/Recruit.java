package baedalmate.baedalmate.recruit.domain;

import baedalmate.baedalmate.category.domain.Category;
import baedalmate.baedalmate.chat.domain.ChatRoom;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.domain.embed.Place;
import baedalmate.baedalmate.review.domain.Review;
import baedalmate.baedalmate.user.domain.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruit {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "recruit_id")
    private Long id;

    @Embedded
    private Place place;

    private Dormitory dormitory;

    private Criteria criteria;

    private Platform platform;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int currentPeople;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int currentPrice;

    private int minPeople;

    private int minPrice;

    private int coupon;

    private boolean freeShipping;

    private LocalDateTime deactivateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruit")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruit", orphanRemoval = true)
    private List<ShippingFee> shippingFees = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruit", orphanRemoval = true)
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "recruit")
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "recruit")
    private ChatRoom chatRoom;

    @CreationTimestamp
    private LocalDateTime createDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;

    private LocalDateTime deadlineDate;

    private String title;

    private String description;

    private String image;

    //    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean active = true;

    private boolean cancel = false;

    private boolean fail = false;

    //== constructor ==//
//    private Recruit() { }

    private Recruit(int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
                    Place place, Platform platform, int coupon, String title, String description, String image, boolean freeShipping) {
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
        this.image = image;
        this.freeShipping = freeShipping;
        this.currentPeople = 1;
    }

    //== 생성 메서드 ==//
    public static Recruit createRecruit(
            User user, Category category,
            int minPeople, int minPrice, LocalDateTime deadlineDate, Criteria criteria, Dormitory dormitory,
            Place place, Platform platform, int coupon, String title, String description, String image, boolean freeShipping,
            int currentPrice,
            List<ShippingFee> shippingFees, List<Tag> tags, List<Order> orders) {
        Recruit recruit = new Recruit(minPeople, minPrice, deadlineDate, criteria, dormitory, place, platform, coupon, title, description, image, freeShipping);
        user.addRecruit(recruit);
        category.addRecruit(recruit);
        for (ShippingFee shippingFee : shippingFees) {
            recruit.addShippingFee(shippingFee);
        }
        for (Tag tag : tags) {
            recruit.addTag(tag);
        }
        for (Order order : orders) {
            recruit.addOrder(order);
        }
        return recruit;
    }

    //== setter ==//

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public void setFreeShipping(Boolean freeShipping) {
        this.freeShipping = freeShipping;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public void setMinPeople(int minPeople) {
        this.minPeople = minPeople;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public void setDormitory(Dormitory dormitory) {
        this.dormitory = dormitory;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCurrentPrice(int currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setCurrentPeople(int currentPeople) {
        this.currentPeople = currentPeople;
    }

    public void setDeactivateDate(LocalDateTime deactivateDate) {
        this.deactivateDate = deactivateDate;
    }

    public void setCoupon(int coupon) {
        this.coupon = coupon;
    }

    //== 연관관계 편의 메서드 ==//
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

    public void addReview(Review review) {
        reviews.add(review);
        review.setRecruit(this);
    }

    //== Getter ==//
    public int getMinShippingFee() {
        if (freeShipping) return 0;

        int min = shippingFees.get(0).getShippingFee();
        for (ShippingFee shippingFee : shippingFees) {
            min = Math.min(shippingFee.getShippingFee(), min);
        }
        return min;
    }

    //== Set Methods ==//
    public int updateCurrentPeople() {
        currentPeople = currentPeople + 1;
        return currentPeople;
    }

    public int updateCurrentPrice(int price) {
        currentPrice = currentPrice + price;
        return currentPrice;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void setShippingFees(List<ShippingFee> shippingFees) {
        this.shippingFees = shippingFees;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}

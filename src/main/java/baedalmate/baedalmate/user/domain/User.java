package baedalmate.baedalmate.user.domain;

import baedalmate.baedalmate.block.domain.Block;
import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.recruit.domain.embed.Address;
import baedalmate.baedalmate.review.domain.Review;
import baedalmate.baedalmate.security.oauth2.soical.SocialType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    private String nickname;
    @Setter
    private String role;

    @Column(columnDefinition = "float default 0", nullable = false)
    private float score;

    private String profileImage;

    @Embedded
    private Address address;

    private SocialType socialType;
    private String socialId;

    @Builder.Default
    private Dormitory dormitory = Dormitory.SUNGLIM;

    @OneToMany(mappedBy = "user")
    private List<Recruit> recruits = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Block> blocks = new ArrayList<>();

    @OneToMany(mappedBy = "target")
    private List<Block> blocked = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Fcm> fcms = new ArrayList<>();

    @CreationTimestamp
    private Timestamp createDate;
    @UpdateTimestamp
    private Timestamp updateDate;

    //== Getter ==//
    public String getAddress() {
        if (address == null) {
            return "";
        }
        if (address.getStreet() == null || address.getDetail() == null || address.getZipcode() == null) {
            return "";
        }
        return address.getStreet() + " " + address.getDetail();
    }

    public String getDormitoryName() {
        if (dormitory == null) {
            return "";
        }
        return dormitory.getName();
    }

    //== 연관관계 편의메서드 ==//
    public void addRecruit(Recruit recruit) {
        recruits.add(recruit);
        recruit.setUser(this);
    }

    public void addReview(Review review) {
        reviews.add(review);
        review.setUser(this);
    }

    public void addBlock(Block block) {
        blocks.add(block);
        block.setUser(this);
    }

    public void addFcm(Fcm fcm) {
        fcms.add(fcm);
        fcm.setUser(this);
    }
}

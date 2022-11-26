package baedalmate.baedalmate.review.domain;

import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.user.domain.User;

import javax.persistence.*;

@Entity
public class Review {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "review_id")
    private Long id;

    private float score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    //== constructor ==//
    private Review() {
    }

    private Review(float score) {
        this.score = score;
    }

    //== 생성 메서드 ==//
    public static Review createReview(float score, User user, User target, Recruit recruit) {
        Review review = new Review(score);
        user.addReview(review);
        recruit.addReview(review);
        review.setTarget(target);
        return review;
    }

    //== setter ==//
    public void setUser(User user) {
        this.user = user;
    }

    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
    }

    public void setTarget(User target) {
        this.target = target;
    }
}

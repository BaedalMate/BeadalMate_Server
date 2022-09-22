package baedalmate.baedalmate.domain;

import javax.persistence.*;

@Entity
public class ShippingFee {

    @GeneratedValue
    @Id
    @Column(name = "shipping_fee_id")
    private Long id;

    private int shippingFee;

    private int lowerPrice;
    private int upperPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    private Recruit recruit;

    //== 연관관계 편의 메서드 ==//
    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}

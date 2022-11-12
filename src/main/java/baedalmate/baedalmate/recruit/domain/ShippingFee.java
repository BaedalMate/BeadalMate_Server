package baedalmate.baedalmate.recruit.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ShippingFee {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "shipping_fee_id")
    private Long id;

    private int shippingFee;

    private int lowerPrice;

    private int upperPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    //== constructor ==//
    private ShippingFee() {}

    private ShippingFee(int shippingFee, int lowerPrice, int upperPrice) {
        this.shippingFee = shippingFee;
        this.lowerPrice = lowerPrice;
        this.upperPrice = upperPrice;
    }

    //== 생성 메서드 ==//
    public static ShippingFee createShippingFee(int shippingFee, int lowerPrice, int upperPrice) {
        return new ShippingFee(shippingFee, lowerPrice, upperPrice);
    }

    //== 연관관계 편의 메서드 ==//
    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
    }
}

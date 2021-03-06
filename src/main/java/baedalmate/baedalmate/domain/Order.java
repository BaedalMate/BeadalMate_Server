package baedalmate.baedalmate.domain;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "orders")
public class Order {
    @GeneratedValue
    @Id
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruit_id")
    private Recruit recruit;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    private List<Menu> menus = new ArrayList<>();

    //== constructor ==//
    private Order() {}

    private Order(User user, Recruit recruit) {
        this.user = user;
        this.recruit = recruit;
    }

    //== 생성 메서드 ==//
    public static Order createOrder(User user, Recruit recruit, List<Menu> menus) {
        Order order = new Order(user, recruit);
        for (Menu menu: menus) {
            order.addMenu(menu);
        }
        return order;
    }
    //== 연관관계 편의 메서드 ==//
    public void setRecruit(Recruit recruit) {
        this.recruit = recruit;
    }

    public void addMenu(Menu menu) {
        menus.add(menu);
        menu.setOrder(this);
    }
}

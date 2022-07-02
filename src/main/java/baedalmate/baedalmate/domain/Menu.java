package baedalmate.baedalmate.domain;

import javax.persistence.*;

@Entity
public class Menu {
    @GeneratedValue
    @Id
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String name;

    private int price;

    //== constructor ==//
    private Menu() {}

    private Menu(String name, int price) {
        this.name = name;
        this.price = price;
    }

    //== 생성 메서드 ==//
    public static Menu createMenu(String name, int price) {
        Menu menu = new Menu(name, price);

        return menu;
    }

    //== 연관관계 편의 메서드 ==//
    public void setOrder(Order order) {
        this.order = order;
    }
}

package baedalmate.baedalmate.domain;

import javax.persistence.*;

@Entity
public class Menu {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "menu_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "menu_name")
    private String name;

    private int price;

    @Column(columnDefinition = "integer default 1")
    private int quantity;

    //== constructor ==//
    private Menu() {}

    private Menu(String name, int price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    //== 생성 메서드 ==//
    public static Menu createMenu(String name, int price, int quantity) {
        Menu menu = new Menu(name, price, quantity);

        return menu;
    }

    //== 연관관계 편의 메서드 ==//
    public void setOrder(Order order) {
        this.order = order;
    }
}

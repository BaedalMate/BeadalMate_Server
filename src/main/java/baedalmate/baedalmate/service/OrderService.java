package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.errors.exceptions.ExistOrderException;
import baedalmate.baedalmate.repository.OrderJpaRepository;
import baedalmate.baedalmate.repository.OrderRepository;
import baedalmate.baedalmate.repository.RecruitJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderJpaRepository orderJpaRepository;
    private final RecruitJpaRepository recruitJpaRepository;

    @Transactional
    public Long createOrder(Recruit recruit, Order order) {
        List<Order> orders = recruit.getOrders();
        // 중복 검사
        List<User> users = orders.stream().map(o -> o.getUser()).collect(Collectors.toList());
        if (users.contains(order.getUser())) {
            throw new ExistOrderException();
        }

        recruit.addOrder(order);
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional
    public int updateCurrentPrice(Order order) {
        List<Menu> menus = order.getMenus();
        Recruit recruit = order.getRecruit();

        int sum = 0;
        for (Menu menu : menus) {
            sum += menu.getPrice() * menu.getQuantity();
        }

        recruitJpaRepository.updateCurrentPrice(sum + recruit.getCurrentPrice(), recruit.getId());
        if (recruit.getCriteria() == Criteria.PRICE && recruit.getCurrentPrice() >= recruit.getMinPrice()) {
            recruit.setActive(false);
        }

        return recruit.getCurrentPrice();
    }
}

package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.*;
import baedalmate.baedalmate.repository.OrderJpaRepository;
import baedalmate.baedalmate.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Long createOrder(Recruit recruit, Order order) {
        recruit.addOrder(order);
        orderRepository.save(order);

        List<Order> orders = orderJpaRepository.findAllByRecruit(recruit);
        // 중복 검사
        List<User> users = orders.stream().map(o -> o.getUser()).collect(Collectors.toList());
        if(orders.size() != users.stream().distinct().count()) {
            // 예외 throw
        }

        // 인원수 검사
        if (recruit.getMinPeople() <= recruit.updateCurrentPeople() && recruit.getCriteria() == Criteria.NUMBER) {
            recruit.setActive(false);
        }

        // 가격 검사
        int currentPrice = 0;
        for (Order o : orders) {
            for (Menu menu : o.getMenus()) {
                currentPrice += menu.getPrice() * menu.getQuantity();
            }
        }
        if (recruit.getCriteria() == Criteria.PRICE && currentPrice >= recruit.getMinPrice()) {
            recruit.setActive(false);
        }
        return order.getId();
    }
}

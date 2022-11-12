package baedalmate.baedalmate.order.service;

import baedalmate.baedalmate.errors.exceptions.ExistOrderException;
import baedalmate.baedalmate.order.dto.CreateOrderDto;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.dto.MenuDto;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
import baedalmate.baedalmate.recruit.domain.Criteria;
import baedalmate.baedalmate.order.domain.Menu;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final RecruitJpaRepository recruitJpaRepository;

    public List<Order> findByRecruitId(Long recruitId) {
        return orderJpaRepository.findAllByRecruitId(recruitId);
    }

    @Transactional
    public Long createOrder(User user, CreateOrderDto createOrderDto) {

        // Recruit 조회
        Recruit recruit = recruitJpaRepository.findById(createOrderDto.getRecruitId()).get();

        // 중복 검사
        List<Order> orders = recruit.getOrders();
        for (Order order : orders) {
            if (order.getUser().getId() == user.getId()) {
                throw new ExistOrderException();
            }
        }

        List<User> users = orders.stream().map(o -> o.getUser()).collect(Collectors.toList());

        // order 생성
        List<Menu> menus = createOrderDto.getMenu().stream()
                .map(m -> Menu.createMenu(m.getName(), m.getPrice(), m.getQuantity()))
                .collect(Collectors.toList());
        Order order = Order.createOrder(user, menus);

        recruit.addOrder(order);
        orderJpaRepository.save(order);

        // current price 갱신
        int price = 0;
        for(MenuDto menuDto : createOrderDto.getMenu()) {
            price += menuDto.getPrice();
        }
        recruitJpaRepository.updateCurrentPrice(recruit.getCurrentPrice() + price, recruit.getId());

        // current people 갱신
        recruitJpaRepository.updateCurrentPeople(recruit.getId());

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

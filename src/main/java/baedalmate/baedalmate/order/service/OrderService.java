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
        return orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId);
    }

    @Transactional
    public void deleteOrder(Long userId, Long recruitId) {
        Order order = orderJpaRepository.findByUserIdAndRecruitIdUsingJoin(userId, recruitId);
        Recruit recruit = order.getRecruit();
        // 현재 인원 감소
        recruitJpaRepository.reduceCurrentPeople(recruit.getId());
        int price = 0;
        List<Menu> menus = order.getMenus();
        for (Menu menu : menus) {
            price += menu.getPrice();
        }
        // 현재 금액 감소
        recruitJpaRepository.updateCurrentPrice(recruit.getCurrentPrice() - price, recruit.getId());
        // order 삭제
        orderJpaRepository.delete(order);
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
        for (MenuDto menuDto : createOrderDto.getMenu()) {
            price += menuDto.getPrice();
        }
        recruitJpaRepository.updateCurrentPrice(recruit.getCurrentPrice() + price, recruit.getId());

        // current people 갱신
        recruitJpaRepository.updateCurrentPeople(recruit.getId());

        return order.getId();
    }
}

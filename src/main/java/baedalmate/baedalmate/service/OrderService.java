package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Order;
import baedalmate.baedalmate.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Long createOrder(Order order) {
        orderRepository.save(order);
        return order.getId();
    }
}

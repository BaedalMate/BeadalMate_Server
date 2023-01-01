package baedalmate.baedalmate.order.dao;

import baedalmate.baedalmate.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public List<Order> findAllByUserIdUsingJoin(Long userId, Pageable pageable) {
        return em.createQuery("select o from Order o join fetch o.recruit join o.recruit.user join o.user " +
                "where o.user.id = :userId and o.user.id != o.recruit.user.id", Order.class)
                .setParameter("userId", userId)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}

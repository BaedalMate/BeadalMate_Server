package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.recruit.domain.Recruit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecruitRepository {
    private final EntityManager em;

    public List<Recruit> findAllByTagUsingJoin(String keyword, Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user join fetch r.tags t " +
                        "where r.active = true and r.cancel = false " +
                        "and t.name like CONCAT('%',:keyword,'%') " +
                        "order by r.deadlineDate ASC", Recruit.class)
                .setParameter("keyword", keyword)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllUsingJoinOrderByScore(Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.active = true and r.cancel = false " +
                        "order by r.user.score DESC", Recruit.class)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllUsingJoinOrderByDeadlineDate(Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.active = true and r.cancel = false " +
                        "order by r.deadlineDate ASC", Recruit.class)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllUsingJoinOrderByView(Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.active = true and r.cancel = false " +
                        "order by r.deadlineDate ASC", Recruit.class)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllByCategoryUsingJoinOrderByScore(Long categoryId, Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.category.id = :id and r.active = true and r.cancel = false " +
                        "order by r.user.score DESC", Recruit.class)
                .setParameter("id", categoryId)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllByCategoryUsingJoinOrderByDeadlineDate(Long categoryId, Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.category.id = :id and r.active = true and r.cancel = false " +
                        "order by r.deadlineDate ASC", Recruit.class)
                .setParameter("id", categoryId)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllByCategoryUsingJoinOrderByView(Long categoryId, Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.category.id = :id and r.active = true and r.cancel = false " +
                        "order by r.view DESC", Recruit.class)
                .setParameter("id", categoryId)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public List<Recruit> findAllWithTagsUsingJoinOrderByDeadlineDate(Dormitory dormitory, Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user join fetch r.tags " +
                        "where r.dormitory = :dormitory and r.active = true and r.cancel = false " +
                        "order by r.deadlineDate ASC", Recruit.class)
                .setParameter("dormitory", dormitory)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public Recruit findByIdUsingJoin(Long id) {
        return em.createQuery("select r from Recruit r join fetch r.user join fetch r.chatRoom " +
                        "where r.id = :id", Recruit.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public Recruit findByIdUsingJoinWithOrder(Long id) {
        return em.createQuery("select r from Recruit r join fetch r.orders " +
                        "where r.id = :id", Recruit.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<Recruit> findByUserIdUsingJoin(Long userId, Pageable pageable) {
        return em.createQuery("select r from Recruit r join fetch r.user " +
                        "where r.user.id = :userId", Recruit.class)
                .setParameter("userId", userId)
                .setFirstResult(pageable.getPageNumber())
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }
}

package baedalmate.baedalmate.repository;


import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.oauth.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public User findByUsername(String name) {
        return em.createQuery("select u from User u where u.name = :name", User.class)
                .setParameter("name", name)
                .getSingleResult();
    }
    // SELECT * FROM user WHERE provider = ?1 and providerId = ?2
    public User findByProviderAndProviderId(String provider, String providerId) {
        return em.createQuery("select u from User u where u.provider = :provider and u.providerId = :providerId", User.class)
                .setParameter("provider", provider)
                .setParameter("providerId", providerId)
                .getSingleResult();
    }
    public User findBySocialTypeAndSocialId(SocialType socialType, String socialId) throws NoResultException {
        return em.createQuery("select u from User u where u.socialType = :socialType and u.socialId = :socialId", User.class)
                .setParameter("socialType", socialType)
                .setParameter("socialId", socialId)
                .getSingleResult();
    }
}

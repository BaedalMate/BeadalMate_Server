package baedalmate.baedalmate.security.repository;

import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.security.oauth2.soical.SocialType;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface AuthRepository extends PagingAndSortingRepository<User, Long> {
    public Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}

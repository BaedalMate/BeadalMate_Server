package baedalmate.baedalmate.security.jwt.service;

import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.security.repository.AuthRepository;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrincipalDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = authRepository.findById(Long.parseLong(username));
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found.");
        }

        return PrincipalDetails.create(user.get());
    }
}

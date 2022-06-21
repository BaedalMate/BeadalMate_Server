package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOne(Long id) {
        return userRepository.findOne(id);
    }
}

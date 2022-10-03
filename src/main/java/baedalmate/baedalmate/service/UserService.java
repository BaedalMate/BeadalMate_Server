package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Dormitory;
import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.dto.UserDto;
import baedalmate.baedalmate.errors.exceptions.InvalidParameterException;
import baedalmate.baedalmate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User findOne(Long id) {
        return userRepository.findOne(id);
    }

    @Transactional
    public User update(Long id, UserDto userDto) {
        User user = userRepository.findOne(id);
        String dormitoryName = userDto.getDormitory();
        switch(dormitoryName){
            case "BURAM":
                user.setDormitory(Dormitory.BURAM);
                break;
            case "SUNGLIM":
                user.setDormitory(Dormitory.SUNGLIM);
                break;
            case "KB":
                user.setDormitory(Dormitory.KB);
                break;
            case "SULIM":
                user.setDormitory(Dormitory.SULIM);
                break;
            case "NURI":
                user.setDormitory(Dormitory.NURI);
                break;
            default:
                throw new InvalidParameterException();
        }
        userRepository.save(user);
        return user;
    }
}

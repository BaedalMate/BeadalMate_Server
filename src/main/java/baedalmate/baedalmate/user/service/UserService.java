package baedalmate.baedalmate.user.service;

import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.errors.exceptions.InvalidParameterException;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;

    public User findOne(Long id) {
        return userJpaRepository.findById(id).get();
    }

    public UserInfoDto getUserInfo(Long id) {
        User user = userJpaRepository.findById(id).get();
        return new UserInfoDto(user.getId(), user.getNickname(), user.getProfileImage(), user.getDormitoryName(), user.getScore());
    }

    @Transactional
    public User updateDormitory(Long id, String dormitory) {
        User user = userJpaRepository.findById(id).get();
        switch(dormitory){
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
                throw new InvalidParameterException("Wrong dormitory name");
        }
        userJpaRepository.save(user);
        return user;
    }
}

package baedalmate.baedalmate.user.service;

import baedalmate.baedalmate.recruit.domain.Dormitory;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.errors.exceptions.InvalidParameterException;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.dto.UpdateUserDto;
import baedalmate.baedalmate.user.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;

    public String updateProfileImage(Long id, MultipartFile profileImage) {
        User user = userJpaRepository.findById(id).get();
        if(!profileImage.isEmpty()) {
            Date date = new Date();
            StringBuilder sb = new StringBuilder();
            String fileName = profileImage.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            // file name format: {date}_{fileOriginalName}.{file type}
            sb.append(date.getTime());
            sb.append("_");
            sb.append(fileName);
            File newFileName = new File(sb.toString());
            try {
                profileImage.transferTo(newFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            user.setProfileImage("/images/" + sb.toString());
            userJpaRepository.save(user);
            return user.getProfileImage();
        }
        return "";
    }

    public UserInfoDto update(Long id, String nickname) {
        User user = userJpaRepository.findById(id).get();

        if(nickname != null && nickname != "") {
            user.setNickname(nickname);
        }
        userJpaRepository.save(user);
        return new UserInfoDto(user.getId(), user.getNickname(), user.getProfileImage(), user.getDormitoryName(), user.getScore());
    }

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
        if(user.getDormitory() == null) {
            user.setRole("USER");
        }
        switch (dormitory) {
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

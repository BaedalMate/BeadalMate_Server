package baedalmate.baedalmate.user.service;

import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;
    private final RecruitJpaRepository recruitJpaRepository;
    private final OrderJpaRepository orderJpaRepository;

    @Transactional
    public void withdrawal(Long id) {
        List<Order> orders = orderJpaRepository.findAllByUserIdUsingJoin(id);
        if (orders.size() > 0) {
            throw new InvalidApiRequestException("User is participating some recruit.");
        }
        User user = userJpaRepository.findById(id).get();
        user.setRole("deactivate");
        user.setNickname("");
        user.setProfileImage("");
        user.setSocialId("");
        userJpaRepository.save(user);
        recruitJpaRepository.setCancelTrueByUserId(id);
    }

    public String updateProfileImage(Long id, MultipartFile profileImage) {
        User user = userJpaRepository.findById(id).get();
        if (!profileImage.isEmpty()) {
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
            user.setProfileImage(sb.toString());
            userJpaRepository.save(user);
            return user.getProfileImage();
        }
        return "";
    }

    public UserInfoDto update(Long id, String nickname, boolean defaultImage, MultipartFile profileImage) {
        User user = userJpaRepository.findById(id).get();
        if (nickname != null) {
            if (nickname.length() > 5) {
                throw new InvalidApiRequestException("Length must be less than 6");
            }
            if (nickname != null && nickname != "") {
                user.setNickname(nickname);
            }
        }
        if (defaultImage == true) {
            user.setProfileImage("default_image.png");
        } else if (profileImage != null) {
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
            user.setProfileImage(sb.toString());
        }
        if (user.getNickname() != "" && user.getDormitory() != null && user.getRole() != "USER") {
            user.setRole("USER");
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
        if (user.getNickname() != "" && user.getDormitory() != null && user.getRole() != "USER") {
            user.setRole("USER");
        }
        userJpaRepository.save(user);
        return user;
    }
}

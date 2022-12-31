package baedalmate.baedalmate.review.service;

import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.domain.Order;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
import baedalmate.baedalmate.recruit.dao.RecruitRepository;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.recruit.dto.ParticipantDto;
import baedalmate.baedalmate.recruit.dto.ParticipantsDto;
import baedalmate.baedalmate.review.dao.ReviewJpaRepository;
import baedalmate.baedalmate.review.domain.Review;
import baedalmate.baedalmate.review.dto.CreateReviewDto;
import baedalmate.baedalmate.review.dto.UserDto;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewJpaRepository reviewJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final RecruitJpaRepository recruitJpaRepository;
    private final RecruitRepository recruitRepository;
    private final OrderJpaRepository orderJpaRepository;

    public ParticipantsDto getTarget(Long userId, Long recruitId) {
        AtomicBoolean participate = new AtomicBoolean(false);
        List<Order> orders = orderJpaRepository.findAllByRecruitIdUsingJoin(recruitId);
        List<ParticipantDto> participants = new ArrayList<>();
        for (Order o : orders) {
            if (o.getUser().getId() == userId) {
                participate.set(true);
                continue;
            }
            participants.add(
                    new ParticipantDto(o.getUser().getId(), o.getUser().getNickname(), o.getUser().getProfileImage())
            );
        }
        if (participate.get() == false) {
            throw new InvalidApiRequestException("User is not participant");
        }
        return new ParticipantsDto(recruitId, participants);
    }

    @Transactional
    public void create(Long userId, CreateReviewDto createReviewDto) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoinWithOrder(createReviewDto.getRecruitId());

        // 참여자 외 인원 후기를 남길 경우예외
        boolean flag = false;
        for(UserDto u : createReviewDto.getUsers()) {
            if(!recruit.getOrders().stream().anyMatch(o -> o.getUser().getId() == u.getUserId())) {
                flag = true;
            }
        }
        if(flag) {
            throw new InvalidApiRequestException("Target is not participant");
        }
        // Order 조회 및 참여자 조사
        for (Order o : recruit.getOrders()) {
            if (o.getUser().getId() == userId) continue;
            boolean reviewed = createReviewDto.getUsers()
                    .stream().anyMatch(u -> u.getUserId() == o.getUser().getId());
            if (!reviewed) {
                throw new InvalidApiRequestException("Review all users");
            }
        }
        // 유저가 참여자인지 확인
        if (!recruit.getOrders().stream().anyMatch(o -> o.getUser().getId() == userId)) {
            throw new InvalidApiRequestException("User is not participant");
        }
        // Recruit 마감 검사
        if (recruit.isActive()) {
            throw new InvalidApiRequestException("Not closed recruit");
        }
        // 이미 후기를 남겼는지 검사
        List<Review> reviews = reviewJpaRepository.findAllByRecruitIdUsingJoin(createReviewDto.getRecruitId());
        boolean reviewed = false;
        for (Review r : reviews) {
            if (r.getUser().getId() == userId)
                reviewed = true;
        }
        if (reviewed) {
            throw new InvalidApiRequestException("Already reviewed");
        }
        //== 리뷰 생성 ==//
        for (UserDto userDto : createReviewDto.getUsers()) {
            User target = userJpaRepository.findById(userDto.getUserId()).get();
            Review review = Review.createReview(userDto.getScore(), user, target, recruit);

            reviewJpaRepository.save(review);
            Double avg = reviewJpaRepository.findAverageFromTargetUsingJoin(target.getId());
            userJpaRepository.updateScore(avg.floatValue(), target.getId());
        }
    }
}

package baedalmate.baedalmate.review.service;

import baedalmate.baedalmate.errors.exceptions.AccessDeniedException;
import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.order.dao.OrderJpaRepository;
import baedalmate.baedalmate.order.domain.Order;
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
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewJpaRepository reviewJpaRepository;
    private final UserJpaRepository userJpaRepository;
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
            throw new AccessDeniedException("User is not participant");
        }
        return new ParticipantsDto(recruitId, participants);
    }

    @Transactional
    public void create(Long userId, CreateReviewDto createReviewDto) {
        // 유저조회
        User user = userJpaRepository.findById(userId).get();
        // Recruit 조회
        Recruit recruit = recruitRepository.findByIdUsingJoin(createReviewDto.getRecruitId());
        // Recruit 마감 검사
        if (recruit.isActive()) {
            throw new InvalidApiRequestException("Not closed recruit");
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

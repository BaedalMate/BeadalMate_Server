package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Review;
import baedalmate.baedalmate.repository.ReviewJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewJpaRepository reviewJpaRepository;
    public Long save(Review review) {
        reviewJpaRepository.save(review);
        return review.getId();
    }
}

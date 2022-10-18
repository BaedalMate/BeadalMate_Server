package baedalmate.baedalmate.repository;


import baedalmate.baedalmate.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {
}

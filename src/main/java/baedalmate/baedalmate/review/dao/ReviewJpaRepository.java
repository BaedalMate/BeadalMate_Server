package baedalmate.baedalmate.review.dao;

import baedalmate.baedalmate.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewJpaRepository extends JpaRepository<Review, Long> {

    @Query("select AVG(r.score) from Review r join r.target where r.target.id = :id")
    Double findAverageFromTargetUsingJoin(@Param("id") Long targetId);
}

package baedalmate.baedalmate.repository;

import baedalmate.baedalmate.domain.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShippingFeeJpaRepository extends JpaRepository<ShippingFee, Long> {

    @Query("select sf from ShippingFee sf join fetch sf.recruit where sf.recruit.id = :id")
    List<ShippingFee> findAllByRecruitIdUsingJoin(@Param("id") Long recruitId);
}

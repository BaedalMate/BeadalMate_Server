package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.domain.ShippingFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShippingFeeJpaRepository extends JpaRepository<ShippingFee, Long> {
    @Modifying(clearAutomatically = true)
    @Query("delete from ShippingFee sf where sf.recruit.id = :id")
    void deleteByRecruitId(@Param("id") Long recruitId);
}

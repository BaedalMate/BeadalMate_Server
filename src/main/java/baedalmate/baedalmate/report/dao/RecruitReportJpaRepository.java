package baedalmate.baedalmate.report.dao;

import baedalmate.baedalmate.report.domain.RecruitReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecruitReportJpaRepository extends JpaRepository<RecruitReport, Long> {

    @Query("select rr from RecruitReport rr join rr.target where rr.target.id = :recruitId")
    List<RecruitReport> findAllByTargetRecruitIdUsingJoin(@Param("recruitId") Long recruitId);
}

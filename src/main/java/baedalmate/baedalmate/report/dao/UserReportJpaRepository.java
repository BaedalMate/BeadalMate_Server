package baedalmate.baedalmate.report.dao;

import baedalmate.baedalmate.report.domain.RecruitReport;
import baedalmate.baedalmate.report.domain.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReportJpaRepository extends JpaRepository<UserReport, Long> {
    @Query("select ur from UserReport ur join ur.target where ur.target.id = :targetUserId")
    List<UserReport> findAllByTargetUserIdUsingJoin(@Param("targetUserId") Long targetUserId);
}

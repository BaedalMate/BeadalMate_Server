package baedalmate.baedalmate.report.service;

import baedalmate.baedalmate.errors.exceptions.InvalidApiRequestException;
import baedalmate.baedalmate.recruit.dao.RecruitJpaRepository;
import baedalmate.baedalmate.recruit.domain.Recruit;
import baedalmate.baedalmate.report.dao.RecruitReportJpaRepository;
import baedalmate.baedalmate.report.dao.UserReportJpaRepository;
import baedalmate.baedalmate.report.domain.RecruitReport;
import baedalmate.baedalmate.report.domain.UserReport;
import baedalmate.baedalmate.report.dto.RecruitReportRequestDto;
import baedalmate.baedalmate.report.dto.UserReportRequestDto;
import baedalmate.baedalmate.user.dao.UserJpaRepository;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserReportJpaRepository userReportJpaRepository;
    private final RecruitReportJpaRepository recruitReportJpaRepository;
    private final RecruitJpaRepository recruitJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public void reportUser(Long userId, UserReportRequestDto userReportRequestDto) {
        User targetUser = userJpaRepository.findById(userReportRequestDto.getTargetUserId()).get();
        List<UserReport> userReports = userReportJpaRepository.findAllByTargetUserIdUsingJoin(userReportRequestDto.getTargetUserId());
        User user = userJpaRepository.findById(userId).get();
        if(userReports.stream().anyMatch(r -> r.getUser().getId() == userId)) {
            throw new InvalidApiRequestException("Already reported");
        }
        if(userReportRequestDto.getTargetUserId() == userId) {
            throw new InvalidApiRequestException("Users cannot report themselves");
        }
        UserReport userReport = UserReport.createUserReport(user, targetUser, userReportRequestDto.getReason(), userReportRequestDto.getDetail());
        userReportJpaRepository.save(userReport);
    }

    public void reportRecruit(Long userId, RecruitReportRequestDto recruitReportRequestDto) {
        Recruit targetRecruit = recruitJpaRepository.findById(recruitReportRequestDto.getTargetRecruitId()).get();
        List<RecruitReport> recruitReports = recruitReportJpaRepository.findAllByTargetRecruitIdUsingJoin(recruitReportRequestDto.getTargetRecruitId());
        User user = userJpaRepository.findById(userId).get();
        if(recruitReports.stream().anyMatch(r -> r.getUser().getId() == userId)) {
            throw new InvalidApiRequestException("Already reported");
        }
        if(targetRecruit.getUser().getId() == userId) {
            throw new InvalidApiRequestException("Users cannot report their own recruit");
        }
        RecruitReport recruitReport = RecruitReport.createRecruitReport(user, targetRecruit, recruitReportRequestDto.getReason(), recruitReportRequestDto.getDetail());
        recruitReportJpaRepository.save(recruitReport);
    }
}

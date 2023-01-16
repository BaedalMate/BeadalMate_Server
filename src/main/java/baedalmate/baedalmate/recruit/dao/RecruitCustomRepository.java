package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.dto.HostedRecruitDto;
import baedalmate.baedalmate.recruit.dto.ParticipatedRecruitDto;
import baedalmate.baedalmate.recruit.dto.RecruitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitCustomRepository {

    Page<RecruitDto> findAllUsingJoinOrderByDeadlineDate(Pageable pageable, Long userId);
    Page<RecruitDto> findAllUsingJoinOrderByView(Pageable pageable, Long userId);
    Page<RecruitDto> findAllUsingJoinOrderByScore(Pageable pageable, Long userId);
    Page<RecruitDto> findAllByCategoryIdUsingJoinOrderByDeadlineDate(Pageable pageable, Long userId, Long categoryId);
    Page<RecruitDto> findAllByCategoryIdUsingJoinOrderByScore(Pageable pageable, Long userId, Long categoryId);
    Page<RecruitDto> findAllByCategoryIdUsingJoinOrderByView(Pageable pageable, Long userId, Long categoryId);
    Page<RecruitDto> findAllByTagUsingJoin(String keyword, Pageable pageable, Long userId);

    Page<ParticipatedRecruitDto> findAllParticipatedRecruitDtoByUserIdUsingJoin(Pageable pageable, Long userId);
    Page<HostedRecruitDto> findAllHostedRecruitDtoByUserIdUsingJoin(Pageable pageable, Long userId);
}

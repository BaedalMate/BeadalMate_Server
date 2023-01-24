package baedalmate.baedalmate.recruit.dao;

import baedalmate.baedalmate.recruit.dto.HostedRecruitDto;
import baedalmate.baedalmate.recruit.dto.ParticipatedRecruitDto;
import baedalmate.baedalmate.recruit.dto.RecruitDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecruitCustomRepository {

    Page<RecruitDto> findAllUsingJoin(Pageable pageable, Long userId, Long categoryId, Boolean exceptClose);
    Page<RecruitDto> findAllByTagUsingJoin(String keyword, Pageable pageable, Long userId);

    Page<ParticipatedRecruitDto> findAllParticipatedRecruitDtoByUserIdUsingJoin(Pageable pageable, Long userId);
    Page<HostedRecruitDto> findAllHostedRecruitDtoByUserIdUsingJoin(Pageable pageable, Long userId);
}

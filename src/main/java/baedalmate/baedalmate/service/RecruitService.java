package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Recruit;
import baedalmate.baedalmate.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitService {

    private final RecruitRepository recruitRepository;

    @Transactional
    public Long createRecruit(Recruit recruit) {
        recruitRepository.save(recruit);
        return recruit.getId();
    }
}

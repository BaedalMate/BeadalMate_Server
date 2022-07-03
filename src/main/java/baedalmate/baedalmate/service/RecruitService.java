package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Recruit;
import baedalmate.baedalmate.repository.RecruitJpaRepository;
import baedalmate.baedalmate.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecruitService {

    private final RecruitRepository recruitRepository;
    private final RecruitJpaRepository recruitJpaRepository;

    @Transactional
    public Long createRecruit(Recruit recruit) {
        recruitRepository.save(recruit);
        return recruit.getId();
    }

    public Page<Recruit> findAll(Pageable pageable) {
        return recruitJpaRepository.findAll(pageable);
    }

    public Recruit findOne(Long id) {
        return recruitRepository.findOne(id);
    }
}

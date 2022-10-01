package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Dormitory;
import baedalmate.baedalmate.domain.Recruit;
import baedalmate.baedalmate.repository.RecruitJpaRepository;
import baedalmate.baedalmate.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    public Recruit findById(Long recruitId) {
        return recruitRepository.findOne(recruitId);
    }

    public List<Recruit> findAll(Pageable pageable) {
        String sort = pageable.getSort().toString();
        if(sort.contains("score")) {
            return recruitJpaRepository.findAllUsingJoinOrderByScore(pageable);
        }
        if(sort.contains("deadlineDate")) {
            return recruitJpaRepository.findAllUsingJoinOrderByDeadlineDate(pageable);
        }
        if(sort.contains("view")) {
            return recruitJpaRepository.findAllUsingJoinOrderByView(pageable);
        }
        return new ArrayList<Recruit>();
    }

    public List<Recruit> findAllWithTag(Dormitory dormitory, Pageable pageable) {
        return recruitJpaRepository.findAllWithTagsUsingJoinOrderByDeadlineDate(dormitory, pageable);
    }

    public List<Recruit> findAllByCategory(Long categoryId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        if(sort.contains("score")) {
            return recruitJpaRepository.findAllByCategoryUsingJoinOrderByScore(categoryId, pageable);
        }
        if(sort.contains("deadlineDate")) {
            return recruitJpaRepository.findAllByCategoryUsingJoinOrderByDeadlineDate(categoryId, pageable);
        }
        if(sort.contains("view")) {
            return recruitJpaRepository.findAllByCategoryUsingJoinOrderByView(categoryId, pageable);
        }
        return new ArrayList<Recruit>();
    }
}

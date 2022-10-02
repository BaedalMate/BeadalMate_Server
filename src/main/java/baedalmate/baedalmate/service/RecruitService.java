package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Dormitory;
import baedalmate.baedalmate.domain.Recruit;
import baedalmate.baedalmate.repository.RecruitJpaRepository;
import baedalmate.baedalmate.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
        return recruitJpaRepository.findById(recruitId).get();
    }

    public Recruit getRecruitDetailById(Long recruitId) {
        Recruit recruit = recruitJpaRepository.findById(recruitId).get();

        recruitJpaRepository.updateView(recruitId);
        return recruit;
    }

    public List<Recruit> findAll(Pageable pageable) {
        String sort = pageable.getSort().toString();
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if(sort.contains("score")) {
            return recruitJpaRepository.findAllUsingJoinOrderByScore(p);
        }
        if(sort.contains("deadlineDate")) {
            return recruitJpaRepository.findAllUsingJoinOrderByDeadlineDate(p);
        }
        if(sort.contains("view")) {
            return recruitJpaRepository.findAllUsingJoinOrderByView(p);
        }
        return new ArrayList<Recruit>();
    }

    public List<Recruit> findAllWithTag(Dormitory dormitory, Pageable pageable) {
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return recruitJpaRepository.findAllWithTagsUsingJoinOrderByDeadlineDate(dormitory, p);
    }

    public List<Recruit> findAllByCategory(Long categoryId, Pageable pageable) {
        String sort = pageable.getSort().toString();
        Pageable p = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        if(sort.contains("score")) {
            return recruitJpaRepository.findAllByCategoryUsingJoinOrderByScore(categoryId, p);
        }
        if(sort.contains("deadlineDate")) {
            return recruitJpaRepository.findAllByCategoryUsingJoinOrderByDeadlineDate(categoryId, p);
        }
        if(sort.contains("view")) {
            return recruitJpaRepository.findAllByCategoryUsingJoinOrderByView(categoryId, p);
        }
        return new ArrayList<Recruit>();
    }
}

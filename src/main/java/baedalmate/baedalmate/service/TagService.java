package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Tag;
import baedalmate.baedalmate.repository.TagJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TagService {
    private final TagJpaRepository tagJpaRepository;

    public List<Tag> findByRecruitId(Long recruitId) {
        return tagJpaRepository.findAllByRecruitIdUsingJoin(recruitId);
    }
}

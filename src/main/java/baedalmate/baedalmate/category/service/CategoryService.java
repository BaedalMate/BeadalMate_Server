package baedalmate.baedalmate.category.service;

import baedalmate.baedalmate.category.domain.Category;
import baedalmate.baedalmate.category.dao.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryJpaRepository categoryJpaRepository;

    public Category findOne(Long id) {
        return categoryJpaRepository.findById(id).get();
    }
}

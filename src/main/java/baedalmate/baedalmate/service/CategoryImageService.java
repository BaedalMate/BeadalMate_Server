package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.CategoryImage;
import baedalmate.baedalmate.repository.CategoryImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryImageService {

    private final CategoryImageJpaRepository categoryImageJpaRepository;

    @Transactional
    public Long createCategoryImage(CategoryImage categoryImage) {
        categoryImageJpaRepository.save(categoryImage);
        return categoryImage.getId();
    }
}

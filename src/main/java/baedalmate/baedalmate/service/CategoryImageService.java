package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Category;
import baedalmate.baedalmate.domain.CategoryImage;
import baedalmate.baedalmate.errors.exceptions.ImageNotFoundException;
import baedalmate.baedalmate.repository.CategoryImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

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

    public CategoryImage getRandomCategoryImage(Category category) {
        List<CategoryImage> categoryImages = categoryImageJpaRepository.findByCategoryId(category.getId());
        if(categoryImages.size()==0){
            throw new ImageNotFoundException();
        }
        Random rand = new Random();
        return categoryImages.get(rand.nextInt(categoryImages.size()));
    }
}

package baedalmate.baedalmate.category.service;

import baedalmate.baedalmate.category.dao.CategoryJpaRepository;
import baedalmate.baedalmate.category.domain.Category;
import baedalmate.baedalmate.category.domain.CategoryImage;
import baedalmate.baedalmate.errors.exceptions.ImageNotFoundException;
import baedalmate.baedalmate.category.dao.CategoryImageJpaRepository;
import baedalmate.baedalmate.image.dto.ImageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryImageService {

    private final CategoryImageJpaRepository categoryImageJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    @Transactional
    public ImageDto createCategoryImage(Long categoryId, MultipartFile[] uploadfile) {
        Category category = categoryJpaRepository.findById(categoryId).get();

        List<String> images = new ArrayList<>();
        for (MultipartFile file : uploadfile) {
            Date date = new Date();
            StringBuilder sb = new StringBuilder();
            String fileName = file.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            // file name format: {date}_{fileOriginalName}.{file type}
            sb.append(date.getTime());
            sb.append("_");
            sb.append(fileName);
            images.add("/images/" + sb.toString());
            File newFileName = new File(sb.toString());
            try {
                file.transferTo(newFileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
            CategoryImage categoryImage = CategoryImage.createCategoryImage(category, sb.toString());
            categoryImageJpaRepository.save(categoryImage);
        }
        return new ImageDto(images);
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

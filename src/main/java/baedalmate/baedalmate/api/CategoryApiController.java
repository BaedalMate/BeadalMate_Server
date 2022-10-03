package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.Category;
import baedalmate.baedalmate.domain.CategoryImage;
import baedalmate.baedalmate.dto.ImageResponse;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.repository.CategoryRepository;
import baedalmate.baedalmate.service.CategoryImageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = {"카테고리 이미지 업로드 api"})
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryRepository categoryRepository;
    private final CategoryImageService categoryImageService;

    @PostMapping(value = "/category/uploads")
    public ImageResponse uploadCategoryImage(
            @CurrentUser PrincipalDetails principalDetails,
            @RequestParam("uploadfile") MultipartFile[] uploadfile,
            @RequestParam("categoryId") Long categoryId
    ) {
        // 카테고리 조회
        Category category = categoryRepository.findOne(categoryId);

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
            categoryImageService.createCategoryImage(categoryImage);
        }
        return new ImageResponse(images);
    }
}
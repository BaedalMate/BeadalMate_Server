package baedalmate.baedalmate.category.api;

import baedalmate.baedalmate.image.dto.ImageDto;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.category.service.CategoryImageService;
import baedalmate.baedalmate.category.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "카테고리 이미지 업로드 api")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryService categoryService;
    private final CategoryImageService categoryImageService;

    @PostMapping(value = "/category/uploads")
    public ResponseEntity<ImageDto> uploadCategoryImage(
            @AuthUser PrincipalDetails principalDetails,
            @RequestParam("uploadfile") MultipartFile[] uploadfile,
            @RequestParam("categoryId") Long categoryId
    ) {
        ImageDto images = categoryImageService.createCategoryImage(categoryId, uploadfile);
        return ResponseEntity.ok().body(images);
    }
}
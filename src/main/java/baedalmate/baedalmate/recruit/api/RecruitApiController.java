package baedalmate.baedalmate.recruit.api;

import baedalmate.baedalmate.errors.exceptions.ResourceNotFoundException;
import baedalmate.baedalmate.recruit.dto.*;
import baedalmate.baedalmate.recruit.service.RecruitService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"모집글 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecruitApiController {

    private final UserService userService;
    private final RecruitService recruitService;

    @ApiOperation(value = "모집글 생성")
    @PostMapping(value = "/recruit/new")
    public ResponseEntity<RecruitIdDto> createRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateRecruitDto createRecruitDto
    ) {
        Long recruitId = recruitService.createRecruit(principalDetails.getId(), createRecruitDto);

        RecruitIdDto response = new RecruitIdDto(recruitId);

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 리스트 조회")
    @GetMapping(value = "/recruit/list")
    public ResponseEntity<RecruitListDto> getRecruitList(
            @ApiParam(value = "카테고리별 조회")
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10)
            @ApiParam(value = "예시: {ip}:8080/recruit/list?page=0&size=5&sort=deadlineDate&categoryId=1")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            })
                    Pageable pageable) {

        List<RecruitDto> recruits;
        if (categoryId == null) {
            recruits = recruitService.findAllRecruitDto(pageable);
        } else {
            recruits = recruitService.findAllByCategory(categoryId, pageable);
        }

        RecruitListDto response = new RecruitListDto(recruits);

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "메인페이지 모집글 리스트 조회")
    @GetMapping(value = "/recruit/main/list")
    public ResponseEntity<RecruitListDto> getMainRecruitList(
            @PageableDefault(size = 5)
            @ApiParam(value = "예시: {ip}:8080/recruit/main/list?page=0&size=5&sort=deadlineDate")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            }) Pageable pageable) {

        List<MainPageRecruitDto> recruitList = recruitService.findAllMainPageRecruitDto(pageable);
        RecruitListDto response = new RecruitListDto(recruitList);

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "메인페이지 태그 포함된 모집글 리스트 조회")
    @GetMapping(value = "/recruit/tag/list")
    public ResponseEntity<RecruitListDto> getTagRecruitList(
            @AuthUser PrincipalDetails principalDetails,
            @PageableDefault(size = 5)
            @ApiParam(value = "예시: {ip}:8080/recruit/tag/list?page=0&size=5&sort=deadlineDate")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        // 유저 정보 조회
        User user = userService.findOne(principalDetails.getId());
        if (user.getDormitory() == null) {
            throw new ResourceNotFoundException();
        }

        List<MainPageRecruitDtoWithTag> recruitList = recruitService.findAllWithTag(user.getDormitory(), pageable);
        RecruitListDto response = new RecruitListDto(recruitList);

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 상세 조회")
    @GetMapping(value = "/recruit/{id}")
    public ResponseEntity<RecruitDetailDto> getRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @ApiParam(value = "모집글 id")
            @PathVariable("id")
                    Long recruitId
    ) {
        // Recruit 조회수 증가
        int view = recruitService.updateView(recruitId);

        RecruitDetailDto response = recruitService.findOne(principalDetails.getUser(), recruitId);

        return ResponseEntity.ok().body(response);
    }
}

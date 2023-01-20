package baedalmate.baedalmate.recruit.api;

import baedalmate.baedalmate.recruit.dto.*;
import baedalmate.baedalmate.recruit.service.RecruitService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.*;
import baedalmate.baedalmate.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "모집글 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class RecruitApiController {

    private final UserService userService;
    private final RecruitService recruitService;

    @Operation(summary = "태그로 모집글 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = RecruitListResponseDto.class))),
    })
    @GetMapping(value = "/recruit/search")
    @CustomPageableAsQueryParam
    public ResponseEntity<RecruitListWithLastAndCount> searchByTag(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "태그 검색 키워드")
            @RequestParam(required = true) String keyword,
            @Parameter(hidden = true)
            @PageableDefault(size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            })
                    Pageable pageable) {
        Page<RecruitDto> recruits = recruitService.findAllByTag(principalDetails.getId(), keyword, pageable);
        RecruitListWithLastAndCount response = new RecruitListWithLastAndCount(recruits.getContent(), recruits.isLast(), recruits.getTotalElements());

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "유저 메뉴 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "참여자가 아닌 경우",
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}")
                            }
                    )),
    })
    @GetMapping(value = "/recruit/{id}/my-menu")
    public ResponseEntity<MyMenuDto> getMyMenu(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "모집글 id") @PathVariable("id") Long recruitId

    ) {
        MyMenuDto response = recruitService.getMyMenu(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 메뉴 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "참여자가 아닌 경우",
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}")
                            }
                    )),
    })
    @GetMapping(value = "/recruit/{id}/menu")
    public ResponseEntity<ParticipantsMenuDto> getMenu(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "모집글 id") @PathVariable("id") Long recruitId

    ) {
        ParticipantsMenuDto response = recruitService.getMenu(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 참여자 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "참여자가 아닌 경우",
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}")
                            }
                    )),
    })
    @GetMapping(value = "/recruit/{id}/participants")
    public ResponseEntity<ParticipantsDto> getParticipants(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "모집글 id") @PathVariable("id") Long recruitId
    ) {
        ParticipantsDto response = recruitService.getParticipants(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = ResultSuccessWithIdResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "호스트가 아닌 경우",
                                            value = "{\"code\": 400, \"message\": \"Not host\"}"),
                                    @ExampleObject(name = "참여자가 있는 경우",
                                            value = "{\"code\": 400, \"message\": \"Someone is participating\"}")
                            }
                    )),
    })
    @PatchMapping(value = "/recruit/{id}")
    public ResponseEntity<Map<String, Object>> updateRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody UpdateRecruitDto updateRecruitDto,
            @Parameter(description = "모집글 id")
            @PathVariable("id") Long recruitId
    ) {
        recruitService.update(principalDetails.getId(), recruitId, updateRecruitDto);

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        response.put("id", recruitId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                            }
                    )),
    })
    @PostMapping(value = "/recruit/new")
    public ResponseEntity<RecruitIdDto> createRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateRecruitDto createRecruitDto
    ) {
        Long recruitId = recruitService.create(principalDetails.getId(), createRecruitDto);

        RecruitIdDto response = new RecruitIdDto(recruitId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 리스트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = RecruitListResponseDto.class))),
    })
    @CustomPageableAsQueryParam
    @GetMapping(value = "/recruit/list")
    public ResponseEntity<RecruitListWithLastDto> getRecruitList(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "카테고리별 조회")
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10)
            @Parameter(hidden = true)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            })
                    Pageable pageable) {

        Page<RecruitDto> recruits;

        if (categoryId == null) {
            recruits = recruitService.findAllRecruitDto(principalDetails.getId(), pageable);
        } else {
            recruits = recruitService.findAllByCategory(principalDetails.getId(), categoryId, pageable);
        }

        RecruitListWithLastDto response = new RecruitListWithLastDto(recruits.getContent(), recruits.isLast());

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "메인페이지 모집글 리스트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MainPageRecruitListResponseDto.class))),
    })
    @CustomPageableAsQueryParam
    @GetMapping(value = "/recruit/main/list")
    public ResponseEntity<RecruitListDto> getMainRecruitList(
            @AuthUser PrincipalDetails principalDetails,
            @PageableDefault(size = 5)
            @Parameter(hidden = true)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            }) Pageable pageable) {

        List<MainPageRecruitDto> recruitList = recruitService.findAllMainPageRecruitDto(principalDetails.getId(), pageable);
        RecruitListDto response = new RecruitListDto(recruitList);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "메인페이지 태그 포함된 모집글 리스트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = MainPageRecruitWithTagListResponseDto.class))),
    })
    @CustomPageableAsQueryParam
    @GetMapping(value = "/recruit/tag/list")
    public ResponseEntity<RecruitListDto> getTagRecruitList(
            @AuthUser PrincipalDetails principalDetails,
            @PageableDefault(size = 5)
            @Parameter(hidden = true)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            }) Pageable pageable) {
        List<MainPageRecruitDtoWithTag> recruitList = recruitService.findAllWithTag(
                principalDetails.getId(),
                pageable);
        RecruitListDto response = new RecruitListDto(recruitList);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
    })
    @GetMapping(value = "/recruit/{id}")
    public ResponseEntity<RecruitDetailDto> getRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "모집글 id")
            @PathVariable("id")
                    Long recruitId
    ) {
        // Recruit 조회수 증가
        int view = recruitService.updateView(recruitId);

        RecruitDetailDto response = recruitService.findOne(principalDetails.getUser(), recruitId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 마감")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 마감 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "호스트가 아닌 경우",
                                            value = "{\"code\": 400, \"message\": \"Not host\"}"),
                                    @ExampleObject(name = "취소된 경우",
                                            value = "{\"code\": 400, \"message\": \"Already canceled recruit\"}"),
                                    @ExampleObject(name = "마감된 경우",
                                            value = "{\"code\": 400, \"message\": \"Already closed recruit\"}"),
                            }
                    )),
    })
    @GetMapping(value = "/recruit/close/{id}")
    public ResponseEntity<Map<String, Object>> closeRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "모집글 id")
            @PathVariable("id")
                    Long recruitId
    ) {
        recruitService.close(recruitId, principalDetails.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "모집글 취소 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "호스트가 아닌 경우",
                                            value = "{\"code\": 400, \"message\": \"Not host\"}"),
                                    @ExampleObject(name = "취소된 경우",
                                            value = "{\"code\": 400, \"message\": \"Already canceled recruit\"}"),
                                    @ExampleObject(name = "마감된 경우",
                                            value = "{\"code\": 400, \"message\": \"Already closed recruit\"}"),
                            }
                    )),
    })
    @GetMapping(value = "/recruit/cancel/{id}")
    public ResponseEntity<Map<String, Object>> cancelRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @Parameter(description = "모집글 id")
            @PathVariable("id")
                    Long recruitId
    ) {
        recruitService.cancel(recruitId, principalDetails.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }
}

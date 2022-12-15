package baedalmate.baedalmate.recruit.api;

import baedalmate.baedalmate.errors.exceptions.ResourceNotFoundException;
import baedalmate.baedalmate.recruit.dto.*;
import baedalmate.baedalmate.recruit.service.RecruitService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.user.domain.User;
import baedalmate.baedalmate.user.service.UserService;
import io.swagger.annotations.*;
import lombok.*;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"모집글 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class RecruitApiController {

    private final UserService userService;
    private final RecruitService recruitService;

    @ApiOperation(value = "유저 메뉴 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 모집글 참여자가 아닌 경우")
    })
    @GetMapping(value = "/recruit/{id}/my-menu")
    public ResponseEntity<ParticipantMenuDto> getMyMenu(
            @AuthUser PrincipalDetails principalDetails,
            @PathVariable("id") Long recruitId

    ) {
        ParticipantMenuDto response = recruitService.getMyMenu(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "태그로 모집글 검색")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: Guest인 경우")
    })
    @GetMapping(value = "/recruit/search")
    public ResponseEntity<RecruitListDto> searchByTag(
            @ApiParam(value = "태그 검색")
            @RequestParam(required = true) String keyword,
            @PageableDefault(size = 10)
            @ApiParam(value = "예시: {ip}:8080/recruit/search?page=0&size=5&sort=deadlineDate&categoryId=0&keyword=태그")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "deadlineDate", direction = Sort.Direction.ASC)
            })
                    Pageable pageable) {
        List<RecruitDto> list = recruitService.findAllByTag(keyword, pageable);
        RecruitListDto response = new RecruitListDto(list);
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 메뉴 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 모집글 참여자가 아닌 경우")
    })
    @GetMapping(value = "/recruit/{id}/menu")
    public ResponseEntity<ParticipantsMenuDto> getMenu(
            @AuthUser PrincipalDetails principalDetails,
            @PathVariable("id") Long recruitId

    ) {
        ParticipantsMenuDto response = recruitService.getMenu(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 참여자 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 모집글 참여자가 아닌 경우")
    })
    @GetMapping(value = "/recruit/{id}/participants")
    public ResponseEntity<ParticipantsDto> getParticipants(
            @AuthUser PrincipalDetails principalDetails,
            @PathVariable("id") Long recruitId
    ) {
        ParticipantsDto response = recruitService.getParticipants(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 수정")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 호스트가 아닌 경우")
    })
    @PatchMapping(value = "/recruit/{id}")
    public ResponseEntity<Map<String, Object>> updateRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody UpdateRecruitDto updateRecruitDto,
            @PathVariable("id") Long recruitId
    ) {
        recruitService.update(principalDetails.getId(), recruitId, updateRecruitDto);

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        response.put("id", recruitId);

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 생성")
    @ApiResponses({
            @ApiResponse(code = 200, message = "생성 성공"),
            @ApiResponse(code = 400, message = "생성 실패: 필수 정보 누락"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 권한이 GUEST인 경우")
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

    @ApiOperation(value = "모집글 리스트 조회")
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 권한이 GUEST인 경우")
    })
    @GetMapping(value = "/recruit/list")
    public ResponseEntity<RecruitListDto> getRecruitList(
            @ApiParam(value = "카테고리별 조회")
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10)
            @ApiParam(value = "예시: {ip}:8080/recruit/list?page=0&size=5&sort=deadlineDate&categoryId=0")
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 권한이 GUEST인 경우")
    })
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 권한이 GUEST인 경우")
    })
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
    @ApiResponses({
            @ApiResponse(code = 200, message = "조회 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 권한이 GUEST인 경우")
    })
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

    @ApiOperation(value = "모집글 마감")
    @ApiResponses({
            @ApiResponse(code = 200, message = "모집글 마감 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 호스트가 아닌 경우")
    })
    @GetMapping(value = "/recruit/close/{id}")
    public ResponseEntity<Map<String, Object>> closeRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @ApiParam(value = "모집글 id")
            @PathVariable("id")
                    Long recruitId
    ) {
        recruitService.close(recruitId, principalDetails.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }

    @ApiOperation(value = "모집글 취소")
    @ApiResponses({
            @ApiResponse(code = 200, message = "모집글 취소 성공"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 호스트가 아닌 경우")
    })
    @GetMapping(value = "/recruit/cancel/{id}")
    public ResponseEntity<Map<String, Object>> cancelRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @ApiParam(value = "모집글 id")
            @PathVariable("id")
                    Long recruitId
    ) {
        recruitService.cancel(recruitId, principalDetails.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }
}

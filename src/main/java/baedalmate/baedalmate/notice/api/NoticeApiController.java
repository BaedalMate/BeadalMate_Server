package baedalmate.baedalmate.notice.api;

import baedalmate.baedalmate.notice.dto.NoticeDetailDto;
import baedalmate.baedalmate.notice.dto.NoticeListDto;
import baedalmate.baedalmate.notice.service.NoticeService;
import baedalmate.baedalmate.recruit.dto.RecruitDto;
import baedalmate.baedalmate.recruit.dto.RecruitListWithLastAndCount;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.CustomPageableAsQueryParam;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공지 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class NoticeApiController {

    private final NoticeService noticeService;

    @Operation(summary = "공지 리스트 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = RecruitListWithLastAndCount.class))),
    })
    @GetMapping(value = "/notice")
    public ResponseEntity<NoticeListDto> noticeList() {
        NoticeListDto response = noticeService.getNoticeList();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "공지 리스트 상세")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = RecruitListWithLastAndCount.class))),
    })
    @GetMapping(value = "/notice/{id}")
    public ResponseEntity<NoticeDetailDto> noticeDetail(
            @PathVariable("id") Long noticeId
    ) {
        NoticeDetailDto response = noticeService.getNoticeDetail(noticeId);
        return ResponseEntity.ok().body(response);
    }
}

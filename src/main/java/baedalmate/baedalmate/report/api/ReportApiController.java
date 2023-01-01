package baedalmate.baedalmate.report.api;

import baedalmate.baedalmate.report.dto.RecruitReportRequestDto;
import baedalmate.baedalmate.report.dto.UserReportRequestDto;
import baedalmate.baedalmate.report.service.ReportService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import baedalmate.baedalmate.swagger.AccessDeniedErrorResponseDto;
import baedalmate.baedalmate.swagger.ExpiredJwtErrorResponseDto;
import baedalmate.baedalmate.swagger.ResultSuccessResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "신고하기 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class ReportApiController {

    private final ReportService reportService;

    @Operation(summary = "유저 신고")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 성공",
                    content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "이미 신고한 경우",
                                            value = "{\"code\": 400, \"message\": \"Already reported\"}"),
                                    @ExampleObject(name = "자기자신을 신고한 경우",
                                            value = "{\"code\": 400, \"message\": \"Users cannot report themselves\"}")
                            }
                    )),
    })
    @PostMapping(value = "/report/user")
    public ResponseEntity<Map> reportUser(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid UserReportRequestDto userReportRequestDto
    ) {
        reportService.reportUser(principalDetails.getId(), userReportRequestDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모집글 신고")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "신고 성공",
                    content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "이미 신고한 경우",
                                            value = "{\"code\": 400, \"message\": \"Already reported\"}"),
                                    @ExampleObject(name = "자신의 모집글을 신고한 경우",
                                            value = "{\"code\": 400, \"message\": \"Users cannot report their own recruit\"}")
                            }
                    )),
    })
    @PostMapping(value = "/report/recruit")
    public ResponseEntity<Map> reportRecruit(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid RecruitReportRequestDto recruitReportRequestDto
    ) {
        reportService.reportRecruit(principalDetails.getId(), recruitReportRequestDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return ResponseEntity.ok().body(response);
    }
}

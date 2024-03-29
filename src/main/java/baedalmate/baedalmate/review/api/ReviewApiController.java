package baedalmate.baedalmate.review.api;

import baedalmate.baedalmate.recruit.dto.ParticipantsDto;
import baedalmate.baedalmate.review.dto.CreateReviewDto;
import baedalmate.baedalmate.review.dto.ReviewTargetListDto;
import baedalmate.baedalmate.review.service.ReviewService;
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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "리뷰 api")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
@ApiResponses({
        @ApiResponse(description = "토큰 만료", responseCode = "401", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExpiredJwtErrorResponseDto.class))),
        @ApiResponse(description = "권한 부족", responseCode = "403", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccessDeniedErrorResponseDto.class)))
})
public class ReviewApiController {
    private final ReviewService reviewService;

    @Operation(summary = "후기 대상자 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "참여자가 아닐 경우",
                                            value = "{\"code\": 400, \"message\": \"Review all users\"}"),
                            }
                    )),
    })
    @GetMapping(value = "/{id}/target")
    public ResponseEntity<ReviewTargetListDto> getReviewTarget(
            @AuthUser PrincipalDetails principalDetails,
            @PathVariable("id") Long recruitId
    ) {
        ReviewTargetListDto response = reviewService.getTarget(principalDetails.getId(), recruitId);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "후기 남기기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "후기 남기 성공", content = @Content(schema = @Schema(implementation = ResultSuccessResponseDto.class))                                                                           ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "필수 정보 누락",
                                            value = "{\"code\": 400, \"message\": \"Api request body invalid\"}"),
                                    @ExampleObject(name = "참여자가 아닐 경우",
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}"),
                                    @ExampleObject(name = "모집글이 마감되지 않은 경우",
                                            value = "{\"code\": 400, \"message\": \"Not closed recruit\"}"),
                                    @ExampleObject(name = "이미 리뷰를 남긴 경우",
                                            value = "{\"code\": 400, \"message\": \"Already reviewed\"}"),
                                    @ExampleObject(name = "모집글 참여자가 아닌 유저에게 리뷰를 남긴 경우",
                                            value = "{\"code\": 400, \"message\": \"Target is not participant\"}"),
                            }
                    )),
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody @Valid CreateReviewDto createReviewDto
    ) {
        reviewService.create(principalDetails.getId(), createReviewDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }
}

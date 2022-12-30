package baedalmate.baedalmate.review.api;

import baedalmate.baedalmate.recruit.dto.ParticipantsDto;
import baedalmate.baedalmate.review.dto.CreateReviewDto;
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
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}"),
                            }
                    )),
    })
    @GetMapping(value = "/{id}/target")
    public ResponseEntity<ParticipantsDto> getReviewTarget(
            @AuthUser PrincipalDetails principalDetails,
            @PathVariable("id") Long recruitId
    ) {
        ParticipantsDto response = reviewService.getTarget(principalDetails.getId(), recruitId);
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
                                    @ExampleObject(name = "참여자가 아닐 경우",
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}"),
                                    @ExampleObject(name = "참여자가 아닐 경우",
                                            value = "{\"code\": 400, \"message\": \"User is not participant\"}"),
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

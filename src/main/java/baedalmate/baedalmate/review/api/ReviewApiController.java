package baedalmate.baedalmate.review.api;

import baedalmate.baedalmate.review.dto.CreateReviewDto;
import baedalmate.baedalmate.review.service.ReviewService;
import baedalmate.baedalmate.security.annotation.AuthUser;
import baedalmate.baedalmate.security.user.PrincipalDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = {"리뷰 api"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewApiController {
    private final ReviewService reviewService;

    @ApiOperation(value = "후기 남기기")
    @ApiResponses({
            @ApiResponse(code = 200, message = "후기 남기 성공"),
            @ApiResponse(code = 400, message = "요청 실패: 마감되지 않은 모집글"),
            @ApiResponse(code = 401, message = "잘못된 토큰"),
            @ApiResponse(code = 403, message = "잘못된 권한: 참여자가 아닌 경우")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(
            @AuthUser PrincipalDetails principalDetails,
            @RequestBody CreateReviewDto createReviewDto
    ) {
        reviewService.create(principalDetails.getId(), createReviewDto);
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");
        return ResponseEntity.ok().body(response);
    }
}

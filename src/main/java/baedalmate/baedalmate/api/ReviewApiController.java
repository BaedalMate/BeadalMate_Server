package baedalmate.baedalmate.api;

import baedalmate.baedalmate.domain.Recruit;
import baedalmate.baedalmate.domain.Review;
import baedalmate.baedalmate.domain.User;
import baedalmate.baedalmate.oauth.annotation.CurrentUser;
import baedalmate.baedalmate.oauth.domain.PrincipalDetails;
import baedalmate.baedalmate.service.RecruitService;
import baedalmate.baedalmate.service.ReviewService;
import baedalmate.baedalmate.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"후기 api"})
@RestController
@RequestMapping(value = "/api/v1")
@RequiredArgsConstructor
public class ReviewApiController {
    private final ReviewService reviewService;
    private final RecruitService recruitService;
    private final UserService userService;

    @ApiOperation(value = "후기 남기기")
    @PostMapping("/review")
    public Map<String, Object> createReview(
            @CurrentUser PrincipalDetails principalDetails,
            @RequestBody ReviewRequest reviewRequest
    ) {
        //== 유저 조회 ==//
        User user = principalDetails.getUser();

        //== 모집글 조회 ==//
        Recruit recruit = recruitService.findById(reviewRequest.getRecruitId());

        //== 리뷰 생성 ==//
        for(UserDto userDto : reviewRequest.getUsers()) {
            User target = userService.findOne(userDto.getUserId());
            Review review = Review.createReview(userDto.getScore(), user, target, recruit);
            reviewService.save(review);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("result", "success");

        return response;
    }

    @Data
    @Schema
    static class ReviewRequest {
        @Schema(description = "모집글 id")
        private Long recruitId;
        private List<UserDto> users;
    }

    @Data
    @Schema
    static class UserDto {
        @Schema(description = "유저 id")
        private Long userId;
        @Schema(description = "후기 점수")
        private float score;
    }
}

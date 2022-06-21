package baedalmate.baedalmate.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = {"모집글 api"})
@RestController
@RequestMapping("/api/v1")
public class RecruitApiController {

    @ApiOperation(value = "모집글 리스트 조회")
    @GetMapping(value = "/recruit/list")
    public Result getRecruitList(
            @ApiParam(value = "카테고리별 조회(일단 사용x)")
            @RequestParam(required = false) Long categoryId,
            @ApiParam(value = "예시: {ip}:8080/production/list?page=0&size=5&sort=view,DESC")
                    Pageable pageable
    ) {
        List<RecruitDto> collect = new ArrayList<>();
        return new Result(collect);
    }

    @Data
    @AllArgsConstructor
    @Schema
    static class Result {
        @Schema(name = "결과 리스트")
        private List<RecruitDto> data;
    }

    @Data
    @Schema
    static class RecruitDto {
        @Schema(name = "해당 모집글 id", example = "1")
        private Long id;

        @Schema(name = "식당 이름", example = "도미노피자")
        private String restaurantName;

        @Schema(name = "최소 주문 금액", example = "15000")
        private int minPrice;

        @Schema(name = "배달비", example = "3000")
        private int deliveryFee;

        @Schema(name = "글 작성 시간")
        private Date createDate;

        @Schema(name = "마감 시간")
        private Date deadlineDate;

        @Schema(name = "예상 배달 시간", example = "20~30분")
        private String estimateDeliveryTime;

        @Schema(name = "유저 평점", example = "4.1")
        private float userScore;
    }
}

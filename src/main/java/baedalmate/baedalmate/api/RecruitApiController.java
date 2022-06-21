package baedalmate.baedalmate.api;

import baedalmate.baedalmate.dto.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    static class RecruitDto {
        private Long id;
        private String restaurantName;
        private int minPrice;
        private int deliveryFee;
        private Date createDate;
        private Date deadlineDate;
        private String estimateDeliveryTime;
        private float userScore;
    }
}

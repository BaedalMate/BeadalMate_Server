package baedalmate.baedalmate.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDto {
    @Schema(description = "장소명", example = "도미노피자 공릉점")
    private String name;// 장소명, 업체명
    @Schema(description = "전체 지번 주소", example = "서울시 노원구 공릉동 100번지")
    private String addressName; // 전체 지번 주소
    @Schema(description = "전체 도로명 주소", example = "서울시 노원구 공릉로 232길")
    private String roadAddressName; // 전체 도로명 주소
    @Schema(description = "x 좌표값 or longitude", example = "10.10 (실수형!)")
    private float x; // X 좌표값 혹은 longitude
    @Schema(description = "y 좌표값 or latitude", example = "10.10 (실수형!)")
    private float y; // Y 좌표값 혹은 latitude
}
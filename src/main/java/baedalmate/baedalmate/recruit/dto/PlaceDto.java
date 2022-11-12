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
    @Schema(description = "장소명")
    private String name;// 장소명, 업체명
    @Schema(description = "전체 지번 주소")
    private String addressName; // 전체 지번 주소
    @Schema(description = "전체 도로명 주소")
    private String roadAddressName; // 전체 도로명 주소
    @Schema(description = "x 좌표값 or longitude")
    private float x; // X 좌표값 혹은 longitude
    @Schema(description = "y 좌표값 or latitude")
    private float y; // Y 좌표값 혹은 latitude
}
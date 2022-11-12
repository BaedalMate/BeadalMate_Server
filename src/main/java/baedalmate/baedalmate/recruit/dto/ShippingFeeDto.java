package baedalmate.baedalmate.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class ShippingFeeDto {
    @Schema(description = "배달비")
    private int shippingFee;
    @Schema(description = "해당 가격 이상")
    private int lowerPrice;
    @Schema(description = "해당 가격 이하")
    private int upperPrice;
}
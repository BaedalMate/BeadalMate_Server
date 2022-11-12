package baedalmate.baedalmate.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class MenuDto {
    @Schema(description = "메뉴명", example = "하와이안피자")
    private String name;
    @Schema(description = "가격", example = "15000")
    private int price;
    @Schema(description = "수량", example = "1")
    private int quantity;
}

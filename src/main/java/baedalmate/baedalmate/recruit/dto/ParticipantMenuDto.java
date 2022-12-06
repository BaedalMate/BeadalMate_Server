package baedalmate.baedalmate.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantMenuDto {
    @Schema(description = "유저 id", example = "1")
    private Long userId;
    @Schema(description = "메뉴")
    private List<MenuDto> menu;
    @Schema(description = "유저 별 총 주문 금액")
    private int userOrderTotal;
}

package baedalmate.baedalmate.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @Schema(description = "모집글 id")
    @NotNull
    private Long recruitId;
    @Schema(description = "메뉴")
    @NotNull
    private List<MenuDto> menu;
}

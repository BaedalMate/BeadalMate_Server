package baedalmate.baedalmate.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class DeleteOrderDto {
    @Schema(description = "모집글 id", example = "1")
    @NotNull
    private Long recruitId;
}

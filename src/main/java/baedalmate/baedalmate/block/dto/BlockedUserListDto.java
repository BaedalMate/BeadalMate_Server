package baedalmate.baedalmate.block.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class BlockedUserListDto {
    @Schema(description = "차단 목록")
    private List<BlockedUserDto> blockList;
}

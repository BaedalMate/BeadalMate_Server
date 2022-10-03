package baedalmate.baedalmate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema
public class UserDto {
    private String nickname;
    private String profileImage;
    private String dormitory;
    private float score;
}

package baedalmate.baedalmate.recruit.dto;

import baedalmate.baedalmate.recruit.api.RecruitApiController;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class RecruitListDto {
    private List<?> recruitList;
}

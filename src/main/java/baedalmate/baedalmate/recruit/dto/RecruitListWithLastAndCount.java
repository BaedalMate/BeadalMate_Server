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
public class RecruitListWithLastAndCount {
    private List<?> recruitList;
    private boolean isLast;
    private long total;
}

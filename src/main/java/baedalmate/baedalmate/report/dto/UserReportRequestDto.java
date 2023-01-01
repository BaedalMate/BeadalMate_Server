package baedalmate.baedalmate.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
@Schema
public class UserReportRequestDto {
    @NotNull
    private Long targetUserId;
    @NotNull
    private String reason;
    private String detail;
}

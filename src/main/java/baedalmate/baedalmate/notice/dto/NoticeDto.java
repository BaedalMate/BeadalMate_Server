package baedalmate.baedalmate.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema
public class NoticeDto {
    @Schema(description = "공지 id", example = "1")
    private Long id;
    @Schema(description = "공지 타이틀", example = "공지 타이틀")
    private String title;
    @Schema(description = "보낸 시간", example = "2022-12-25 11:11:11")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;

}

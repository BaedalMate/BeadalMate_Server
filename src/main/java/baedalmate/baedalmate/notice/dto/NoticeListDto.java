package baedalmate.baedalmate.notice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema
public class NoticeListDto {

    private List<NoticeDto> noticeList;
}

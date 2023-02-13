package baedalmate.baedalmate.notice.service;

import baedalmate.baedalmate.notice.dao.NoticeJpaRepository;
import baedalmate.baedalmate.notice.domain.Notice;
import baedalmate.baedalmate.notice.dto.NoticeDetailDto;
import baedalmate.baedalmate.notice.dto.NoticeDto;
import baedalmate.baedalmate.notice.dto.NoticeListDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeJpaRepository noticeJpaRepository;

    public NoticeListDto getNoticeList() {
        List<NoticeDto> noticeDtoList = noticeJpaRepository.findAll().stream()
                .map(n -> new NoticeDto(n.getId(), n.getTitle(), n.getCreateDate()))
                .collect(Collectors.toList());
        return new NoticeListDto(noticeDtoList);
    }

    public NoticeDetailDto getNoticeDetail(Long id) {
        Notice notice = noticeJpaRepository.findById(id).get();
        return new NoticeDetailDto(notice.getTitle(), notice.getDescription(), notice.getCreateDate());
    }
}

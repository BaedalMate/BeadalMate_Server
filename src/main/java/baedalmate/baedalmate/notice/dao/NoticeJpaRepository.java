package baedalmate.baedalmate.notice.dao;

import baedalmate.baedalmate.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {

}

package baedalmate.baedalmate.chat.service;

import baedalmate.baedalmate.chat.domain.Message;
import baedalmate.baedalmate.chat.dao.MessageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {

    private final MessageJpaRepository messageJpaRepository;

    @Transactional
    public Long save(Message message) {
        messageJpaRepository.save(message);
        return message.getId();
    }
}

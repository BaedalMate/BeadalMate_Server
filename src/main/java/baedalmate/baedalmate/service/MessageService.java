package baedalmate.baedalmate.service;

import baedalmate.baedalmate.domain.Message;
import baedalmate.baedalmate.repository.MessageJpaRepository;
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

    public List<Message> findAllByUserId(Long userId) {
        return messageJpaRepository.findAllByUserIdUsingJoin(userId);
    }
}

package baedalmate.baedalmate.fcm.eventListener;

import baedalmate.baedalmate.fcm.Event.ChatEvent;
import baedalmate.baedalmate.fcm.service.FcmService;
import baedalmate.baedalmate.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


// EventListener
@Component
@Async("chat")
@Transactional
@RequiredArgsConstructor
public class ChatEventHandler {
    private final FcmService fcmService;

    @EventListener
    public void handleChatEvent(ChatEvent chatEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = chatEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, chatEvent.getTitle(), chatEvent.getMessage(), chatEvent.getImage());
        }
    }
}

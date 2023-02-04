package baedalmate.baedalmate.fcm.eventListener;

import baedalmate.baedalmate.fcm.event.*;
import baedalmate.baedalmate.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// EventListener
@Component
@Async("close")
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomEventListener {
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
            fcmService.sendByTokenList(fcmTokenList, chatEvent.getTitle(), chatEvent.getMessage(), chatEvent.getImage(), "chat", chatEvent.getChatRoomId());
            log.debug(chatEvent.getTitle());
        }
    }
    @EventListener
    public void handleCloseEvent(CloseEvent closeEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = closeEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, closeEvent.getTitle(), closeEvent.getDescription(), closeEvent.getImage(), "close", closeEvent.getChatRoomId());
        }
    }

    @EventListener
    public void handleFailEvent(FailEvent failEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = failEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, failEvent.getTitle(), failEvent.getDescription(), failEvent.getImage(), "fail", failEvent.getChatRoomId());
        }
    }

    @EventListener
    public void handleCancelEvent(CancelEvent cancelEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = cancelEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, cancelEvent.getTitle(), cancelEvent.getDescription(), cancelEvent.getImage(), "cancel", cancelEvent.getChatRoomId());
        }
    }

    @EventListener
    public void handleParticipateEvent(ParticipateEvent participateEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = participateEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, participateEvent.getTitle(), participateEvent.getDescription(), participateEvent.getImage(), "participate", participateEvent.getChatRoomId());
        }
    }

    @EventListener
    public void handleMenuEvent(MenuEvent menuEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = menuEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, menuEvent.getTitle(), menuEvent.getDescription(), menuEvent.getImage(), "menu", menuEvent.getChatRoomId());
        }
    }
    @EventListener
    public void handleWithdrawalEvent(WithdrawalEvent withdrawalEvent) {
        // 로그아웃 안한 회원의 fcmToken 뽑기
        // 로그아웃한 회원들의 fcmToken 필드는 "" 공백입니다.
        List<String> fcmTokenList = withdrawalEvent.getFcmList()
                .stream()
                .map(f -> f.getFcmToken()).collect(Collectors.toList());

        // 로그아웃 안한 대상에게 알림 보내기
        if (fcmTokenList.size() != 0) {
            fcmService.sendByTokenList(fcmTokenList, withdrawalEvent.getTitle(), withdrawalEvent.getDescription(), withdrawalEvent.getImage(), "withdrawal", withdrawalEvent.getChatRoomId());
        }
    }
}

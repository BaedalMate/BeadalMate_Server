package baedalmate.baedalmate.chat.subscribeEvent;

import baedalmate.baedalmate.user.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketDisconnectHandler implements ApplicationListener<SessionDisconnectEvent> {

    private final RedisService redisService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
        Long userId = (Long) auth.getPrincipal();
//        log.debug(sessionId + ": " + userId);
        log.debug(userId.toString() + " 제거");
        redisService.delValues(userId.toString());
    }
}
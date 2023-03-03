package baedalmate.baedalmate.chat.subscribeEvent;

import baedalmate.baedalmate.user.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSubscribeHandler<S> implements ApplicationListener<SessionSubscribeEvent> {

    private final RedisService redisService;

    @Override
    public void onApplicationEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) accessor.getUser();
        String destination = accessor.getHeader("simpDestination").toString();
        String roomId = destination.split("/")[4];
        Long userId = (Long) auth.getPrincipal();
        if(destination.contains("/topic/chat/room/")){
            redisService.setValues(userId.toString(), roomId);
        } else if(destination.contains("/topic/chat/user/")) {
            redisService.setValues(userId.toString(), "list");
        }
    }
}
package baedalmate.baedalmate.fcm.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmService {

    @Value("${fcm.key.path}")
    private String FCM_PRIVATE_KEY_PATH;

    //
    // 메시징만 권한 설정
    @Value("${fcm.key.scope}")
    private String fireBaseScope;

    // fcm 기본 설정 진행
    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(
                            GoogleCredentials
//                                    .fromStream(new ClassPathResource(FCM_PRIVATE_KEY_PATH).getInputStream())
                                    .fromStream(new FileInputStream(FCM_PRIVATE_KEY_PATH))
                                    .createScoped(List.of(fireBaseScope)))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            // spring 뜰때 알림 서버가 잘 동작하지 않는 것이므로 바로 죽임
            throw new RuntimeException(e.getMessage());
        }
    }


    // 알림 보내기
    public void sendByTokenList(List<String> tokenList, String title, String description, String image, String type, Long chatRoomId) {

        // 메시지 만들기
        // fcm multiMessage 객체 생성 하여 보냄
        MulticastMessage message = MulticastMessage.builder()
                .putData("title", title)
                .putData("body", description)
                .putData("image", image)
                .putData("chatRoomId", chatRoomId.toString())
                .putData("type", type)
                .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                .addAllTokens(tokenList)
//                .addToken(token)  // 단일 토큰일 경우
                .build();

//        Map<String, String> data = new HashMap<>();
//        data.put("time", LocalDateTime.now().toString());
//        data.put("image", image);
//        data.put("type", type);
//        if(chatRoomId != null) {
//            data.put("chatRoomId", chatRoomId.toString());
//        }
//        List<Message> messages = tokenList.stream().map(token -> Message.builder()
//                .putAllData(data)
//                .setNotification(Notification.builder().setTitle(title).setBody(description).setImage(image).build())
//                .setToken(token)
//                .build()).collect(Collectors.toList());
        // 요청에 대한 응답을 받을 response
        BatchResponse response;
        try {

            // 알림 발송
            response = FirebaseMessaging.getInstance().sendMulticast(message);
//            failMessage(tokenList, response);
            // 요청에 대한 응답 처리
            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                List<String> failedTokens = new ArrayList<>();

                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        failedTokens.add(tokenList.get(i));
                    }
                }
                log.error("List of tokens are not valid FCM token : " + failedTokens);
            }
        } catch (FirebaseMessagingException e) {
            log.error("cannot send to memberList push message. error info : {}", e.getMessage());
        }
    }
//    // 토큰이 유효하지 않아서 fcm 발송이 실패한 데이터 추출
//    private void failMessage(List<String> mergeTokenList, BatchResponse response) {
//        if (response.getFailureCount() > 0) {
//            List<SendResponse> responses = response.getResponses();
//            List<String> failedTokens = new ArrayList<>();
//            for (int i = 0; i < responses.size(); i++) {
//                if (!responses.get(i).isSuccessful()) {
//                    failedTokens.add(mergeTokenList.get(i));
//                }
//            }
//            log.info("======================= failedTokens : " + failedTokens + "=======================(추후 실패한 토큰은 삭제 시켜줘야함) -> 쓸데없는 알람이 가서 성능 저하를 일으킴");
//        }
//        log.info("======================= Success : " + response + "=======================");
//    }
}

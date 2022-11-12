package baedalmate.baedalmate.user.service;

import baedalmate.baedalmate.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate redisTemplate;
    private final AppProperties appProperties;

    // 로그아웃 용
    public void logout(String token) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        long time = appProperties.getAuth().getTokenExpirationMsec();
        values.set(token, "logout", Duration.ofMillis(time));
    }

    // 키-벨류 설정
    // 리프레시 토큰을 이용한 토큰 재발급 용
    // 리프레시 토큰 및 유저아이디 등록
    public void setValues(String token, String id){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        long time = appProperties.getAuth().getRefreshTokenExpirationMsec();
        values.set(token, id, Duration.ofMillis(time));
    }

    // 키값으로 벨류 가져오기
    public String getValues(String token){
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(token);
    }

    // 키-벨류 삭제
    public void delValues(String token) {
        redisTemplate.delete(token);
    }
}

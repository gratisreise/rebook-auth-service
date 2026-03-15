package com.example.rebookauthservice.external.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "refresh:";

    @Value("${jwt.refresh-validity}")
    private long refreshTokenValidity;


    public void save(String userId, String refreshToken) {
        redisTemplate.opsForValue()
            .set(PREFIX + userId,
                refreshToken,
                refreshTokenValidity,
                TimeUnit.MILLISECONDS);
    }

    public String find(String userId) {
        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public void delete(String userId) {
        redisTemplate.delete(PREFIX + userId);
    }
}

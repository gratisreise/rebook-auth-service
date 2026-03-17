package com.example.rebookauthservice.external.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "BL:";

    @Value("${jwt.refresh-validity}")
    private long refreshTokenValidity;

    public void addBlack(String accessToken) {
        redisTemplate.opsForValue()
            .set(PREFIX + accessToken,
                accessToken,
                refreshTokenValidity,
                TimeUnit.MILLISECONDS);
    }

    public String find(String accessToken) {
        return redisTemplate.opsForValue().get(PREFIX + accessToken);
    }

}

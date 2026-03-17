package com.example.rebookauthservice.domain.service;

import com.example.rebookauthservice.common.exception.AuthException;
import com.example.rebookauthservice.domain.model.entity.AuthUser;
import com.example.rebookauthservice.domain.repository.AuthRepository;
import com.example.rebookauthservice.common.security.JwtProvider;
import com.rebook.common.auth.HmacUtil;
import com.rebook.common.auth.PassportProto;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassportService {

    private final JwtProvider jwtProvider;
    private final AuthRepository authRepository;
    private final HmacUtil hmacUtil;


    @Value("${passport.validity}")
    private long EXPIRATION;

    //패스포트 발급
    public String issuePassport(String token) {
        if(token == null || token.isBlank()){
            throw AuthException.unknown("토큰이 비어있습니다.");
        }
        String userId = jwtProvider.getAccessUserId(token);

        return generatePassport(userId);
    }

    //패스포트 생성
    private String generatePassport(String userId){

        AuthUser user = authRepository.findByUserId(userId)
            .orElseThrow(() -> AuthException.unknown("사용자를 찾을 수 없습니다."));

        long now = Instant.now().getEpochSecond();


        PassportProto.Passport unsigned = PassportProto.Passport.newBuilder()
            .setPassportId(UUID.randomUUID().toString())
            .setUserId(userId)
            .setIssuedAt(now)
            .setExpiresAt(now + EXPIRATION)
            .build();

        byte[] rawData = unsigned.toByteArray();
        return hmacUtil.sign(rawData);
    }
}

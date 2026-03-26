package com.example.rebookauthservice.domain.service;


import com.example.rebookauthservice.common.security.JwtProvider;
import com.example.rebookauthservice.domain.model.dto.response.RefreshResponse;
import com.example.rebookauthservice.domain.model.dto.response.TokenResponse;
import com.example.rebookauthservice.external.redis.RedisTokenService;
import com.rebook.common.core.exception.BusinessException;
import com.rebook.common.core.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {
  private final JwtProvider jwtProvider;
  private final RedisTokenService redisTokenService;

  // 로그인 토큰 생성
  public TokenResponse generateToken(String memberId) {
    String accessToken = jwtProvider.createAccessToken(memberId);
    String refreshToken = jwtProvider.createRefreshToken(memberId);
    redisTokenService.saveRefreshToken(memberId, refreshToken);
    return TokenResponse.of(accessToken, refreshToken);
  }

  // 토큰재발급
  public RefreshResponse reissueToken(String refreshToken) {
    // 1. 토큰 검증 & memberId 추출
    jwtProvider.validateRefreshToken(refreshToken);

    // 2. memberId 추출
    String memberId = jwtProvider.getRefreshUserId(refreshToken);

    // 3. Redis 대조 (저장된 RT와 일치하는지)
    String storedRT = redisTokenService.getRefreshToken(memberId);

    if (!refreshToken.equals(storedRT)) {
      throw new BusinessException(ErrorCode.FILE_EMPTY);
    }

    // 4. 기존 RT 삭제 (블랙리스트 대신)
    redisTokenService.deleteRefreshToken(memberId);

    // 5. 새 토큰 발급
    String newAT = jwtProvider.createAccessToken(memberId);
    String newRT = jwtProvider.createRefreshToken(memberId);
    redisTokenService.saveRefreshToken(memberId, newRT);

    return RefreshResponse.of(newAT, newRT);
  }

  public static String extractToken(String token) {
    String authPrefix = "Bearer ";
    return token.replace(authPrefix, "");
  }
}

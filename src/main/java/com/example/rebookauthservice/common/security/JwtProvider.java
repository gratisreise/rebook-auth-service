package com.example.rebookauthservice.common.security;

import com.rebook.common.core.exception.BusinessException;
import com.rebook.common.core.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private static final long ACCESS_VALIDITY = 1_800_000L;
    private static final long REFRESH_VALIDITY = 604_800_000L;
    private static final String USER_ID = "userId";

    public JwtProvider(
        @Value("${jwt.access_secret}") String accessKey,
        @Value("${jwt.refresh_secret}") String refreshKey) {
        // HMAC SHA-512 알고리즘을 사용하는 키 생성
        this.accessKey = Keys.hmacShaKeyFor(accessKey.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshKey.getBytes());
    }

    // [ AccessToken ]
    public String createAccessToken(String memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_VALIDITY);

        return Jwts.builder()
            .subject(memberId)
            .claim(USER_ID, memberId)
            .issuedAt(now)
            .expiration(validity)
            .signWith(accessKey, Jwts.SIG.HS512)
            .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser()
            .verifyWith(accessKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String getAccessUserId(String token) {
        return Jwts.parser()
            .verifyWith(accessKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get(USER_ID, String.class);
    }

    public long getExpiration(String accessToken) {
        Date expiration =
            Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getExpiration();
        long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser().verifyWith(accessKey).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
    }

    // [ RefreshToken ]
    public String createRefreshToken(String memberId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_VALIDITY);

        return Jwts.builder()
            .subject(memberId)
            .claim(USER_ID, memberId)
            .issuedAt(now)
            .expiration(validity)
            .signWith(refreshKey, SIG.HS512)
            .compact();
    }

    public String getRefreshSubject(String token) {
        return Jwts.parser()
            .verifyWith(refreshKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String getRefreshUserId(String token) {
        return Jwts.parser()
            .verifyWith(refreshKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get(USER_ID, String.class);
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parser().verifyWith(refreshKey).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }
    }
}

package com.example.rebookauthservice.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    @Value("${jwt.access-validity}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-validity}")
    private long refreshTokenValidity;

    public JwtProvider(
        @Value("${jwt.access-secret}") String acceess,
        @Value("${jwt.refresh-secret}") String refresh
    ) {
        this.accessKey = Keys.hmacShaKeyFor(acceess.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refresh.getBytes());
    }

    public String createAccessToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity);
        return Jwts.builder()
            .subject(userId)
            .issuedAt(now)
            .expiration(validity)
            .signWith(accessKey, Jwts.SIG.HS512) // 0.12.x 버전의 새로운 서명 방식
            .compact();
    }

    public String createRefreshToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
            .subject(userId)
            .issuedAt(now)
            .expiration(validity)
            .signWith(refreshKey, Jwts.SIG.HS512)
            .compact();
    }

    public String getRefreshUserId(String token){
        return Jwts.parser()
            .verifyWith(refreshKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    public String getAccessUserId(String token){
        return Jwts.parser()
            .verifyWith(accessKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

}

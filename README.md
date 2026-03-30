# Rebook Auth Service

[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue.svg)](https://gradle.org/)

Rebook 플랫폼의 인증/인가 마이크로서비스. JWT 기반 인증과 소셜 로그인(OAuth2)을 제공하며, Protobuf + HMAC 기반 Passport 토큰으로 서비스 간 인증을 처리합니다.

## 아키텍처
- 소셜로그인
![소셜로그인](https://diagrams-noaahh.s3.ap-northeast-2.amazonaws.com/auth_social.png)
- Passport 발급 
![passport](https://diagrams-noaahh.s3.ap-northeast-2.amazonaws.com/auth_passport.png)
---

## 기능

### 인증

| 타입 | 설명 |
|------|------|
| 이메일/비밀번호 | 회원가입, 로그인, 로그아웃 |
| 소셜 로그인 | Google, Kakao, Naver OAuth2 인증 |
| 토큰 관리 | Access Token (30분), Refresh Token (7일) |
| Passport | Protobuf 기반 서비스 간 인증 토큰 발급 |

### 주요 기능

- **JWT 인증**: HS512 서명 기반 Access/Refresh 토큰 발급
- **토큰 블랙리스트**: 로그아웃 시 Redis에 Access Token 등록
- **Passport 발급**: Protobuf 직렬화 + HMAC 서명으로 서비스 간 신원 증명
- **OAuth2 Factory Pattern**: `@OAuthServiceType` 어노테이션 기반 Provider별 서비스 자동 매핑

---

## 기술 스택

### Language & Framework
- **Java 17**, **Spring Boot 3.3**, **Spring Security**

### Database
- **PostgreSQL**, **Spring Data JPA**

### Caching & Token Store
- **Redis** — Refresh Token 저장, Access Token 블랙리스트 관리

### Inter-Service Auth
- **Protobuf** — Passport 토큰 직렬화
- **HMAC** — Passport 서명 및 검증

### Cloud & Microservices
- **Spring Cloud Config** — 중앙 설정 관리
- **Eureka Client** — 서비스 디스커버리
- **OpenFeign** — User 서비스 HTTP 통신

### Monitoring
- **Actuator**, **Prometheus**, **Sentry**

### Build & Deploy
- **Gradle**, **Docker**

---

## API 문서
Apidog에서 확인하실 수 있습니다:

```
https://x6wq8qo61i.apidog.io/
```

### Auth API (`/api/auth`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/auth/sign-up` | 회원가입 |
| POST | `/api/auth/login` | 이메일/비밀번호 로그인 |
| POST | `/api/auth/oauth/login` | 소셜 로그인 (Google, Kakao, Naver) |
| POST | `/api/auth/logout` | 로그아웃 (토큰 블랙리스트 등록) |
| POST | `/api/auth/refresh` | Refresh Token으로 Access Token 재발급 |

### Passport API (`/passports`)

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/passports` | JWT 기반 Passport 토큰 발급 |

---

## 프로젝트 구조

```
src/main/java/com/example/rebookauthservice/
├── clientfeign/              # 외부 서비스 통신 (UserClient, NotificationClient)
├── common/
│   ├── annotation/           # 커스텀 어노테이션 (@OAuthServiceType, @Password)
│   ├── enums/                # 열거형 (OauthProvider, Role)
│   ├── exception/            # 예외 처리
│   ├── security/             # Security 설정, JWT Provider, UserDetails
│   └── validation/           # 비밀번호 검증
├── config/                   # WebConfig
├── domain/
│   ├── controller/           # REST API (Auth, Passport)
│   ├── model/
│   │   ├── dto/              # Request/Response DTO
│   │   └── entity/           # AuthUser 엔티티
│   ├── repository/           # Reader/Writer 분리 패턴
│   └── service/              # 비즈니스 로직, OAuth 서비스 구현체
└── external/
    ├── oauth/                # OAuth Provider API 클라이언트 (Google, Kakao, Naver)
    └── redis/                # Redis 설정, 토큰 저장소
```
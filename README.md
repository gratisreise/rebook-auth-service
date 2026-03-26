# Rebook Auth Service

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-brightgreen)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.5-blue)
![Java](https://img.shields.io/badge/Java-17-orange)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-336791)
![Redis](https://img.shields.io/badge/Redis-6+-DC382D)

**Rebook Auth Service**는 중고 도서 거래 플랫폼 Rebook의 인증/인가 마이크로서비스입니다.

Spring Security 기반으로 구현되었으며, JWT 토큰 기반 인증과 다중 OAuth2 프로바이더(Google, Naver, Kakao)를 지원합니다. Redis를 활용한 분산 세션 관리로 확장 가능한 보안 구조를 제공합니다.

## 목차

- [개요](#1-개요)
- [아키텍처](#2-아키텍처)
- [기능](#3-기능)
- [기술 스택](#4-기술-스택)
- [시작하기](#5-시작하기)
- [API 문서](#6-api-문서)
- [설정](#7-설정)
- [개발 가이드](#8-개발-가이드)
- [모니터링](#9-모니터링)
- [문제 해결](#10-문제-해결)
- [기여하기](#11-기여하기)
- [라이선스](#12-라이선스)

---

## 1. 개요

---

## 2. 아키텍처

### 인증 흐름

#### 전통적인 로그인
![basic_login](https://rebook-bucket.s3.ap-northeast-2.amazonaws.com/rebook/basic_login.png)

#### OAuth2 로그인
![oauth_login](https://rebook-bucket.s3.ap-northeast-2.amazonaws.com/rebook/oauth_login.png)

### Netflix Passport
![netflix_passport](https://rebook-bucket.s3.ap-northeast-2.amazonaws.com/rebook/netflix_passport.png)

---

## 3. 기능

### 3.1 인증 시스템

| 기능 | 설명 |
|------|------|
| 회원가입 | Username/Password 기반 가입, BCrypt 암호화 |
| 로그인 | Spring Security AuthenticationManager 기반 인증 |
| OAuth2 | Google, Naver, Kakao 소셜 로그인 (Factory Pattern) |

### 3.2 토큰 관리

| 기능 | 설명 |
|------|------|
| Access Token | JWT 발급 (유효기간: 30분) |
| Refresh Token | JWT 발급 (유효기간: 7일), Redis 캐싱 |
| 토큰 갱신 | Refresh Token 기반 Access Token 재발급 |

### 3.3 패스포트 시스템

- 인증된 사용자에 대한 패스포트 발급
- JWT 토큰 검증을 통한 접근 권한 확인
- 서비스 간 인증 정보 전달

### 3.4 서비스 간 통신

- **User Service**: OpenFeign으로 사용자 생성/조회
- **Eureka**: 서비스 디스커버리로 동적 서비스 탐색

---

## 4. 기술 스택

**Framework & Language**: Java 17, Spring Boot 3.3.13, Spring Security, Spring Data JPA

**Database & Caching**: PostgreSQL 13+, Redis 6+

**Authentication**: JJWT 0.12.5 (JWT 토큰 생성/검증)

**Microservices**: Spring Cloud 2023.0.5 (Eureka Client, Config Client, OpenFeign)

**Monitoring**: Spring Actuator, Prometheus, Sentry 8.13.2, SpringDoc OpenAPI 2.6.0

**Build & Test**: Gradle 8.14.2, JaCoCo

---

## 5. 폴더 구조

```
src/main/java/com/example/rebookauthservice/
├── clientfeign/         # 외부 서비스 통신 (Feign)
│   ├── notification/
│   └── user/
├── common/              # 공통 모듈
│   ├── security/
│   ├── enums/
│   ├── annotation/
│   ├── exception/
│   └── validation/
├── external/            # 외부 연동
│   └── redis/
└── domain/              # 도메인
    ├── controllers/
    ├── repository/
    ├── model/
    │   ├── entity/
    │   └── dto/
    │       ├── request/
    │       └── response/
    └── service/
        ├── reader/
        └── writer/
```

---

## 5. 시작하기

### 5.1 사전 요구사항

- **Java**: JDK 17 이상
- **Gradle**: 8.14.2 이상
- **PostgreSQL**: 13 이상
- **Redis**: 6 이상
- **Spring Cloud Config Server**: 중앙 설정 서버 실행 중
- **Eureka Server**: 서비스 디스커버리 실행 중
- **GitHub 계정**: GitHub Packages 접근용 Personal Access Token (read:packages 권한)

### 5.2 환경 변수 설정

GitHub Packages 인증을 위해 다음 환경 변수를 설정하세요:

```bash
export GITHUB_ACTOR=your-github-username
export GITHUB_TOKEN=your-personal-access-token
```

### 5.3 설치 및 실행

```bash
# 1. 저장소 클론
git clone https://github.com/gratisreise/rebook-auth-service.git
cd rebook-auth-service

# 2. 의존성 설치 및 빌드
./gradlew build

# 3. 애플리케이션 실행 (dev 프로파일)
./gradlew bootRun
```

### 5.4 Docker 실행 (선택사항)

```bash
# OCI 컨테이너 이미지 빌드
./gradlew bootBuildImage

# Docker로 실행
docker run -p 8080:8080 rebook-auth-service:latest
```

---

## 6. API 문서

### 6.1 인증 엔드포인트 (`/api/auth`)

모든 인증 엔드포인트는 인증 없이 접근 가능합니다 (공개 접근 허용).

#### 회원가입

```http
POST /api/auth/sign-up
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "SecurePass123!"
}
```

**응답**: `204 No Content`

#### 로그인

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "SecurePass123!"
}
```

**응답**:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### OAuth 소셜 로그인

```http
POST /api/auth/oauth/login
Content-Type: application/json

{
  "provider": "GOOGLE",  // GOOGLE, NAVER, KAKAO
  "authorizationCode": "4/0AX4XfWh..."
}
```

**응답**: 로그인과 동일

#### 로그아웃

```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

**응답**: `204 No Content`

#### 토큰 갱신

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**응답**:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### 헬스 체크

```http
GET /api/auth
```

**응답**: `"테스트성공"`

### 6.2 패스포트 엔드포인트 (`/passports`)

인증이 필요한 엔드포인트입니다.

#### 패스포트 발급

```http
POST /passports?jwt={accessToken}
```

**응답**: 패스포트 문자열

### 6.3 Swagger UI

API 문서는 Swagger UI를 통해 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui.html
```

---

## 7. 설정

### 7.1 Spring Cloud Config

이 서비스는 Spring Cloud Config Server에서 중앙 집중식 설정을 관리합니다.

**Config Server**: `http://rebook-config:8888`

Config Server가 unavailable인 경우 로컬 설정으로 fallback됩니다 (`optional:configserver`).

### 7.2 필수 설정 항목

Config Server에 다음 설정이 있어야 합니다:

```yaml
jwt:
  access-secret: {JWT_ACCESS_SECRET}
  refresh-secret: {JWT_REFRESH_SECRET}
  access-validity: 1800000  # 30분 (밀리초)
  refresh-validity: 604800000  # 7일 (밀리초)

spring:
  datasource:
    url: jdbc:postgresql://{DB_HOST}:5432/{DB_NAME}
    username: {DB_USERNAME}
    password: {DB_PASSWORD}

  data:
    redis:
      host: {REDIS_HOST}
      port: 6379
      password: {REDIS_PASSWORD}

oauth:
  google:
    client-id: {GOOGLE_CLIENT_ID}
    client-secret: {GOOGLE_CLIENT_SECRET}
    redirect-uri: {GOOGLE_REDIRECT_URI}
  naver:
    client-id: {NAVER_CLIENT_ID}
    client-secret: {NAVER_CLIENT_SECRET}
    redirect-uri: {NAVER_REDIRECT_URI}
  kakao:
    client-id: {KAKAO_CLIENT_ID}
    client-secret: {KAKAO_CLIENT_SECRET}
    redirect-uri: {KAKAO_REDIRECT_URI}
```

### 7.3 OAuth 프로바이더 설정

새로운 OAuth 프로바이더를 추가하려면:

1. `model/dto/oauth/{provider}/`에 프로바이더별 DTO 생성
2. `AbstractOAuthService`를 상속하는 서비스 구현
3. `@OAuthServiceType(Provider.YOUR_PROVIDER)` 어노테이션 추가
4. `Provider` enum에 새 프로바이더 추가
5. Factory가 시작 시 자동으로 서비스를 등록합니다

---

## 8. 개발 가이드

### 8.1 빌드 명령어

```bash
# 프로젝트 빌드
./gradlew build

# 클린 빌드
./gradlew clean build

# 실행 가능한 JAR 생성
./gradlew bootJar
```

### 8.2 테스트

```bash
# 모든 테스트 실행
./gradlew test

# 테스트 + 커버리지 리포트 생성
./gradlew test jacocoTestReport

# 특정 테스트 클래스 실행
./gradlew test --tests com.example.rebookauthservice.YourTestClass

# 특정 테스트 메서드 실행
./gradlew test --tests com.example.rebookauthservice.YourTestClass.testMethod
```

**커버리지 리포트**: `build/reports/jacoco/test/html/index.html`

### 8.3 코드 품질

```bash
# 모든 검증 작업 실행 (테스트 + 커버리지)
./gradlew check
```

### 8.4 개발 환경

```bash
# 개발 모드로 실행 (Hot Reload 활성화)
./gradlew bootRun
```

---

## 9. 모니터링

### 9.1 Spring Boot Actuator

모든 Actuator 엔드포인트가 노출되어 있습니다:

```bash
# 애플리케이션 상태
curl http://localhost:8080/actuator/health

# 메트릭
curl http://localhost:8080/actuator/metrics

# 모든 엔드포인트 목록
curl http://localhost:8080/actuator
```

### 9.2 Prometheus 메트릭

Micrometer Prometheus 레지스트리가 설정되어 있습니다:

```
http://localhost:8080/actuator/prometheus
```

### 9.3 Sentry 에러 추적

Sentry가 자동으로 에러를 추적하고 보고합니다 (빈 설정 필요).

---

## 10. 문제 해결

### 10.1 GitHub Packages 인증 실패

**문제**: 빌드 시 `com.rebook:passport-common:1.0.0`을 찾을 수 없음

**해결책**:
```bash
# 환경 변수 확인
echo $GITHUB_ACTOR
echo $GITHUB_TOKEN

# Personal Access Token에 read:packages 권한이 있는지 확인
```

### 10.2 Config Server 연결 실패

**문제**: Config Server에 연결할 수 없음

**해결책**:
- Config Server가 실행 중인지 확인
- `application-dev.yaml`의 config server URL 확인
- `optional:configserver`로 설정되어 있으므로 로컬 설정으로 fallback 가능

### 10.3 Redis 연결 실패

**문제**: Redis 연결 타임아웃

**해결책**:
```bash
# Redis 실행 확인
redis-cli ping

# Redis 비밀번호 확인
# Config Server의 spring.data.redis.password 확인
```

### 10.4 데이터베이스 스키마 문제

**문제**: JPA 자동 생성으로 인한 스키마 불일치

**해결책**:
- 개발 환경: `ddl-auto: update` 사용
- 운영 환경: `ddl-auto: validate` 또는 `none`으로 변경 권장

---

## 11. 기여하기

### 11.1 개발 프로세스

1. 이 저장소를 fork합니다
2. 기능 브랜치를 생성합니다 (`git checkout -b feature/amazing-feature`)
3. 변경사항을 커밋합니다 (`git commit -m 'Add amazing feature'`)
4. 브랜치에 push합니다 (`git push origin feature/amazing-feature`)
5. Pull Request를 생성합니다

### 11.2 코딩 컨벤션

- Spotless 플러그인으로 코드 포맷팅이 자동 적용됩니다
- 모든 테스트가 통과해야 합니다 (`./gradlew check`)
- 새로운 기능에는 테스트 코드를 작성해야 합니다
- JaCoCo 커버리지 기준을 충족해야 합니다

### 11.3 커밋 메시지 규칙

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅 (기능 변경 없음)
refactor: 코드 리팩토링
test: 테스트 추가/수정
chore: 빌드 프로세스 또는 보조 도구 변경
```

---

## 12. 라이선스

이 프로젝트는 비공개 저장소입니다. 무단 사용, 복사, 배포가 금지되어 있습니다.

---

## 13. 연락처

프로젝트 관리자: gratisreise

이슈나 질문이 있으시면 GitHub Issues를 사용해 주세요.

---

## 14. 추가 리소스

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/3.3.13/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [JJWT Documentation](https://github.com/jwtk/jjwt)

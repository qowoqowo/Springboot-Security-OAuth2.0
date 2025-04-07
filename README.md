# Springboot-Security-OAuth2.0

# 🔐 Spring Boot Security OAuth2 로그인 예제

Spring Boot 3.4.4 기반의 기본 로그인 + OAuth2 로그인(Google, Facebook, Naver) 연동 예제입니다.

## 🛠 기술 스택

- Java 17
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- OAuth2 Client
- MySQL
- Mustache (View Template)
- Lombok

## 🔑 주요 기능

- ✅ Form 기반 로그인 (기본 로그인)
- 🌐 OAuth2 로그인 (Google / Facebook / Naver)
- 🔐 인증 / 인가 처리
- 📦 JPA 기반 사용자 관리
- 🧪 Spring Security 테스트 지원

## ⚙️ 의존성 (pom.xml 요약)

```xml
<dependencies>
    <!-- Web & View -->
    <dependency>spring-boot-starter-web</dependency>
    <dependency>spring-boot-starter-mustache</dependency>

    <!-- Security -->
    <dependency>spring-boot-starter-security</dependency>
    <dependency>spring-boot-starter-oauth2-client</dependency>

    <!-- JPA & MySQL -->
    <dependency>spring-boot-starter-data-jpa</dependency>
    <dependency>mysql-connector-j</dependency>

    <!-- Lombok -->
    <dependency>lombok</dependency>

    <!-- Test -->
    <dependency>spring-boot-starter-test</dependency>
    <dependency>spring-security-test</dependency>
</dependencies>
🧾 OAuth2 설정 예시 (application.yml)

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: [구글 클라이언트 ID]
            client-secret: [구글 시크릿]
            scope:
              - profile
              - email
          facebook:
            client-id: [페이스북 클라이언트 ID]
            client-secret: [페이스북 시크릿]
            scope:
              - public_profile
              - email
          naver:
            client-id: [네이버 클라이언트 ID]
            client-secret: [네이버 시크릿]
            client-name: Naver
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
            scope:
              - name
              - email
            provider: naver
        provider:
          naver:
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            token-uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: https://openapi.naver.com/v1/nid/me
            user-name-attribute: response
📁 프로젝트 구조 예시

└── com.cos.security
    ├── config
    │   ├── SecurityConfig.java
    │   └── oauth
    │       ├── PrincipalOauth2UserService.java
    │       └── provider
    │           ├── GoogleUserInfo.java
    │           ├── FacebookUserInfo.java
    │           └── NaverUserInfo.java
    ├── controller
    ├── model
    ├── repository
    └── service

🚀 실행 방법
MySQL 실행 및 DB 생성 (security)

application.yml DB 설정 작성

OAuth2 클라이언트 ID/시크릿 설정

프로젝트 실행 (SecurityApplication.java)

🙌 참고
스프링부트 + 시큐리티 + OAuth2.0 커스터마이징 연습용 예제입니다.

OAuth 로그인 시, 최초 로그인 사용자 DB 저장 로직 포함 가능합니다 (OAuth2UserService 커스터마이징).

✍️ 작성자
GitHub: https://github.com/qowoqowo

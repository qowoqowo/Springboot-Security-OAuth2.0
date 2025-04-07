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
```

🧾 OAuth2 설정 예시 (application.yml)
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google: # /oauth2/authorization/google 이 주소를 동작하게 한다.
            client-id: [구글 클라이언트 ID]
            client-secret: [구글 시크릿]
            scope:
            - email
            - profile
            
          facebook:
            client-id: [페이스북 클라이언트 ID]
            client-secret: [페이스북 시크릿]
            scope:
            - email
            - public_profile
          
          # 네이버는 OAuth2.0 공식 지원대상이 아니라서 provider 설정이 필요하다.
          # 요청주소도 다르고, 응답 데이터도 다르기 때문이다.
          naver:
            client-id: [네이버 클라이언트 ID]
            client-secret: [네이버 시크릿]
            scope:
            - name
            - email
            - profile_image
            client-name: Naver # 클라이언트 네임은 구글 페이스북도 대문자로 시작하더라.
            authorization-grant-type: authorization_code
            redirect-uri: http://localhost:8080/login/oauth2/code/naver

        provider:
          naver:
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            token-uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: https://openapi.naver.com/v1/nid/me
            user-name-attribute: response # 회원정보를 json의 response 키값으로 리턴해줌.
```
⚙️ 핵심 설정 코드
SecurityConfig.java
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/user/**").authenticated()
            .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll()
        );

        http.formLogin(form -> form
            .loginPage("/loginForm")
            .loginProcessingUrl("/login")
            .defaultSuccessUrl("/")
        );

        http.oauth2Login(oauth2 -> oauth2
            .loginPage("/loginForm")
            .userInfoEndpoint(userInfo -> userInfo
                .userService(principalOauth2UserService)
            )
        );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```
PrincipalOauth2UserService.java
```java
@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();

        if (provider.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        }

        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        String password = bCryptPasswordEncoder.encode("tempPassword");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username).orElseGet(() -> {
            User newUser = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .provider(provider)
                .providerId(oAuth2UserInfo.getProviderId())
                .build();
            return userRepository.save(newUser);
        });

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}

```

📁 프로젝트 구조 예시
```text
src/
└── main/
    ├── java/
    │   └── com.cos.security/
    │       ├── config/
    │       │   ├── auth/
    │       │   │   ├── PrincipalDetails.java
    │       │   │   └── PrincipalDetailsService.java
    │       │   ├── oauth/
    │       │   │   ├── provider/
    │       │   │   │   ├── FacebookUserInfo.java
    │       │   │   │   ├── GoogleUserInfo.java
    │       │   │   │   ├── NaverUserInfo.java
    │       │   │   │   └── OAuth2UserInfo.java
    │       │   │   └── PrincipalOauth2UserService.java
    │       │   ├── AppConfig.java
    │       │   ├── SecurityConfig.java
    │       │   └── WebMvcConfig.java
    │       ├── controller/
    │       │   └── IndexController.java
    │       ├── model/
    │       │   └── User.java
    │       ├── repository/
    │       │   └── UserRepository.java
    │       └── SecurityApplication.java
    ├── resources/
    │   ├── static/
    │   ├── templates/
    │   │   ├── index.html
    │   │   ├── joinForm.html
    │   │   └── loginForm.html
    │   └── application.yml
```

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

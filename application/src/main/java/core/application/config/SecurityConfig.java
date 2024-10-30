package core.application.config;

import core.application.filter.CustomLoginFilter;
import core.application.filter.CustomLogoutFilter;
import core.application.filter.JWTFilter;
import core.application.security.auth.CustomUserDetailsService;
import core.application.security.oauth.CustomOAuth2UserService;
import core.application.security.oauth.CustomOAuthSuccessHandler;
import core.application.security.service.JwtAuthenticationEntryPoint;
import core.application.security.token.JwtTokenUtil;
import core.application.security.token.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

/**
 * Spring Security 구성 클래스
 * <p>
 * 인증, 인가 및 보안 필터를 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuthSuccessHandler customSuccessHandler;
    private final JwtTokenUtil jwtUtil;
    private final JwtAuthenticationEntryPoint entryPoint;

    /**
     * 의존성 주입을 위한 생성자.
     *
     * @param authenticationConfiguration AuthenticationConfiguration 객체
     * @param userDetailsService 사용자 세부 정보 서비스를 위한 객체
     * @param customOAuth2UserService OAuth 사용자 세부 정보 서비스를 위한 객체
     * @param jwtUtil JWT 관련 작업을 위한 유틸리티 클래스
     * @param customSuccessHandler OAuth 인증 성공 시 access token을 반환하는 핸들러
     */
    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration,
            CustomUserDetailsService userDetailsService,
            CustomOAuth2UserService customOAuth2UserService, JwtTokenUtil jwtUtil,
            JwtAuthenticationEntryPoint entryPoint, CustomOAuthSuccessHandler customSuccessHandler) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.userDetailsService = userDetailsService;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtUtil = jwtUtil;
        this.entryPoint = entryPoint;
        this.customSuccessHandler = customSuccessHandler;
    }

    /**
     * AuthenticationManager 빈 등록
     *
     * @param configuration AuthenticationConfiguration 객체
     * @return AuthenticationManager
     * @throws Exception AuthenticationManager 생성 중 오류가 발생할 경우
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 비밀번호를 인코딩하기 위한 BCryptPasswordEncoder 빈 등록
     *
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 보안 필터 체인을 구성
     * <p>
     * CSRF, 폼 로그인, HTTP 기본 인증을 비활성화 로그인, JWT, 로그아웃을 위한 사용자 정의 필터를 추가
     *
     * @param http         HttpSecurity 객체
     * @param tokenService 토큰 처리를 위한 서비스
     * @return 구성된 SecurityFilterChain
     * @throws Exception 필터 체인 구성 중 오류가 발생할 경우
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenService tokenService)
            throws Exception {
        // csrf 보호 비활성화
        http.csrf(AbstractHttpConfigurer::disable);

        // 폼 로그인 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP 기본 인증 비활성화
        http.httpBasic(AbstractHttpConfigurer::disable);

        // OAuth2
        http.oauth2Login((outh2) -> outh2
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler))
                .addFilterAfter(new JWTFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), CustomLoginFilter.class);

        // 다양한 엔드포인트에 대한 인가 규칙 정의
        http.authorizeRequests((auth) -> auth

                // 영화
                .requestMatchers(HttpMethod.GET, "/movies/*/comments")
                .permitAll() // 영화 한줄평 조회
                .requestMatchers(HttpMethod.GET, "/movies/*")
                .permitAll() // 영화 내용 상세 조회
                .requestMatchers(HttpMethod.GET, "/movies/list")
                .permitAll() // 영화 목록 조회 (메인 페이지)
                .requestMatchers(HttpMethod.GET, "/movies/search")
                .permitAll() // 영화 검색
                .requestMatchers(HttpMethod.GET, "/movies/genre/*")
                .permitAll() // 영화 검색

                // 리뷰
                .requestMatchers(HttpMethod.GET, "/movies/*/reviews/list")
                .permitAll() // 리뷰 목록 조회
                .requestMatchers(HttpMethod.GET, "/movies/*/reviews/*")
                .permitAll() // 리뷰 상세 조회
                .requestMatchers(HttpMethod.GET, "/movies/*/reviews/list")
                .permitAll() // 리뷰 목록 조회
                .requestMatchers(HttpMethod.GET, "/movies/*/reviews/*/comments")
                .permitAll() // 리뷰 댓글 목록 조회
                .requestMatchers(HttpMethod.GET, "/movies/*/reviews/*/comments/*")
                .permitAll() // 리뷰 대댓글 목록 조회
                .requestMatchers(HttpMethod.GET, "/movies/*/comments")
                .permitAll() // 한줄평 목록 조회

                // 유저
                .requestMatchers("/users/signup", "/users/signin", "users/reissue")
                .permitAll() // 회원가입, 로그인, refresh token 기반으로 access token 재발급

                // 스웨거
                .requestMatchers("/swagger-ui/*", "/api-test", "/v3/api-docs/**")
                .permitAll()

                // ETC
                .requestMatchers("/error")
                .permitAll() // handle 하지 못한 에러 처리되는 URL
                .anyRequest()
                .authenticated()); // 나머지 요청은 인증 필요

        // JWT 필터를 사용자 정의 로그인 필터 앞에 추가
        http
                .addFilterBefore(new JWTFilter(tokenService), CustomLoginFilter.class)
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint));

        // 사용자 정의 로그인 필터 추가
        http
                .addFilterAt(
                        new CustomLoginFilter(authenticationManager(authenticationConfiguration),
                                jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 사용자 정의 로그아웃 필터 추가
        http
                .addFilterBefore(new CustomLogoutFilter(tokenService), LogoutFilter.class);

        // 세션 관리를 상태 비저장으로 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // AuthenticationManager 를 사용자 세부 정보 서비스와 함께 구성
        AuthenticationManagerBuilder auth = http.getSharedObject(
                AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());

        return http.build();
    }
}





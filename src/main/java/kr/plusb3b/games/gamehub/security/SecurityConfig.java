package kr.plusb3b.games.gamehub.security;

import kr.plusb3b.games.gamehub.api.jwt.JwtAuthenticationFilter;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 비밀번호 암호화 방식 설정 (BCrypt 사용)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtProvider jwtProvider;

    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    List<String> permitPath = new ArrayList<String>();



    // Spring Security 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin()) // ✅ 핵심 설정
                )
                // 모든 요청에 대해 인증 없이 허용
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/board/api/v1/**").authenticated() // ✅ 인증 필요
                                .requestMatchers("/game/api/v1/**").authenticated()
                                .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable())

                // 2. 로그인 설정 (form 기반)
//                .formLogin(formLogin -> formLogin
//                        // 커스텀 로그인 페이지 경로 지정
//                        .loginPage("/login/login-form")
//                        // 로그인 성공 시 이동할 기본 경로 (항상 /index로 이동)
//                        .defaultSuccessUrl(".game-hub", true)
//                        // 로그인 페이지 접근은 누구나 가능
//                        .permitAll()
//                )

                // 3. 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout") // 기본값도 /logout
                        .logoutSuccessUrl("/") // 성공 후 이동
                        .addLogoutHandler((request, response, auth) -> {
                            // ✅ 쿠키 삭제 수동 처리
                            ResponseCookie deleteCookie = ResponseCookie.from("jwt", "")
                                    .httpOnly(true)
                                    .secure(false)
                                    .path("/")
                                    .sameSite("Strict")
                                    .maxAge(0)
                                    .build();

                            response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
                        })
                )
                // 4. CSRF 설정 (쿠키 기반으로 토큰 저장)
                .csrf(csrf -> csrf
                        // CSRF 토큰을 쿠키에 저장하고, JS에서 접근 가능하도록 설정 (HttpOnly=false)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                // 5. JWT 인증 필터 등록 (기존 UsernamePasswordAuthenticationFilter 앞에 삽입)
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        // 설정을 기반으로 SecurityFilterChain 객체 생성 및 반환
        return http.build();
    }


}

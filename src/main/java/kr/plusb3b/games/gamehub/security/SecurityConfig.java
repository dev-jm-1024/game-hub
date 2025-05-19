package kr.plusb3b.games.gamehub.security;

import kr.plusb3b.games.gamehub.api.jwt.JwtAuthenticationFilter;
import kr.plusb3b.games.gamehub.api.jwt.JwtProvider;
import kr.plusb3b.games.gamehub.repository.userrepo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                // 1. URL 요청별 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 누구나 접근 가능한 경로 (비회원 허용) -- 게시판 이용불가
                        .requestMatchers(
                                //"/game-hub/**", "/boards/**", "/mypage/**","/"
                                "/", "/mypage/**", "/game-hub/**", "/games/**", "/boards/**/**"
                        ).permitAll()

                        .requestMatchers("/board/*/new").authenticated()

                        .requestMatchers("/admin").hasRole("ADMIN") //관리자 권한만 접근 가능

                        // 그 외 모든 요청은 "USER" 권한을 가진 사용자만 접근 가능
                        .anyRequest().hasRole("USER")
                )

                // 2. 로그인 설정 (form 기반)
                .formLogin(formLogin -> formLogin
                        // 커스텀 로그인 페이지 경로 지정
                        .loginPage("/login")
                        // 로그인 성공 시 이동할 기본 경로 (항상 /index로 이동)
                        .defaultSuccessUrl("/index", true)
                        // 로그인 페이지 접근은 누구나 가능
                        .permitAll()
                )

                // 3. 로그아웃 설정
                .logout(logout -> logout
                        // 로그아웃 성공 시 이동할 경로
                        .logoutSuccessUrl("/")
                        // 로그아웃 요청은 누구나 가능
                        .permitAll()
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

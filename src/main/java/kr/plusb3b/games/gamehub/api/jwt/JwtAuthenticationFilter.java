package kr.plusb3b.games.gamehub.api.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//요청 헤더에 포함된 JWT 토큰을 꺼내서 검증하고, 사용자 인증 정보를 Spring Security에 등록해주는 필터
//JwtAuthenticationFilter는 모든 요청에 대해 JWT 토큰이 있는지 검사하고, 있으면 사용자 인증 객체를 만들어주는 필터
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    //OncePerRequestFilter: HTTP 요청마다 단 한 번 실행되는 필터
    //Spring Security 필터 체인에 등록되면, 모든 요청에 대해 JWT 검사 담당

    private final JwtProvider jwtProvider;

    // JwtProvider 의존성 주입 (토큰 검증용)
    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. HTTP 요청 헤더에서 Authorization 값 추출
        String authHeader = request.getHeader("Authorization");

        // 2. Authorization 헤더가 존재하고 "Bearer "로 시작하면
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // 3. "Bearer " 다음부터가 실제 토큰 → substring(7)
            String token = authHeader.substring(7);

            // 4. 토큰이 유효한 경우만 인증 처리
            if (jwtProvider.validateToken(token)) {

                // 5. 토큰에서 사용자 ID 추출 (subject 또는 custom claim)
                String userId = jwtProvider.getUserId(token);

                // 6. 인증 객체 생성 (권한은 비워둠)
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());

                // 7. SecurityContext에 인증 정보 저장 (이후 Spring Security가 인증된 사용자로 인식)
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 8. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }
}


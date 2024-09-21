package org.boot.dontspike.JWT;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.parser.Authorization;
import org.boot.dontspike.DTO.CustomOAuth2User;
import org.boot.dontspike.DTO.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String authorization = null;
        Cookie[] cookies = request.getCookies(); // Cookie 리스트
        String requestUri = request.getRequestURI();

        if (requestUri.matches("^\\/login(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }
        if (requestUri.matches("^\\/oauth2(?:\\/.*)?$")) {

            filterChain.doFilter(request, response);
            return;
        }

        // 쿠키가 null인지 확인하고 로그 기록
        if (cookies != null) {
            System.out.println("Cookies received for URI: " + requestUri);
            for (Cookie cookie : cookies) {
                System.out.println("Cookie Name: " + cookie.getName()); // 쿠키 이름 로그 출력
                if (cookie.getName().equals("Authorization")) {
                    authorization = cookie.getValue();
                    System.out.println("Authorization token found: " + authorization); // 토큰 값 출력
                }
            }
        } else {
            System.out.println("No cookies received for URI: " + requestUri);
        }

        // Authorization 헤더 검증
        if (authorization == null) {
            System.out.println("Token null for URI: " + requestUri);
            filterChain.doFilter(request, response);
            return; // 조건이 해당되면 메소드 종료(필수)
        }

        // 토큰 만료 시간 검증
        if (jwtUtil.isExpired(authorization)) {
            System.out.println("Token expired for URI: " + requestUri);
            filterChain.doFilter(request, response);
            return; // 조건이 해당되면 메소드 종료(필수)
        }

        // 토큰에서 username, role 획득
        String username = jwtUtil.getUsername(authorization);
        String role = jwtUtil.getRole(authorization);
        System.out.println("Extracted from token - username: " + username + ", role: " + role);

        // userDTO를 생성하여 값 설정
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);

        // UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDTO);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());

        // 세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

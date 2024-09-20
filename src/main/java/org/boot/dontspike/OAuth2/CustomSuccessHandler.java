package org.boot.dontspike.OAuth2;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.boot.dontspike.DTO.CustomOAuth2User;
import org.boot.dontspike.JWT.JWTUtil;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    public CustomSuccessHandler(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // OAuth2User 정보 추출
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        // JWT 생성
        String token = jwtUtil.createJwt(username, role, 60 * 60 * 60L);

        // 쿠키 설정
        ResponseCookie responseCookie = ResponseCookie.from("Authorization", token)
                .httpOnly(true)
                .secure(true)  // HTTPS 연결을 통해서만 전송
                .sameSite("None")  // 크로스 사이트 요청을 허용
                .path("/")  // 모든 경로에서 접근 가능
                .maxAge(60 * 60 * 60)  // 쿠키의 만료 시간 설정
                .build();

        // 쿠키를 응답에 추가
        response.addCookie(createCookie("Authorization", token));

        // 리다이렉트 처리
        response.sendRedirect("https://dontspike.vercel.app/main");
    }


    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value); //쿠키 선언및 생성
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setSecure(true); //-> https통신에서만 쿠키사용가능한 코드
        cookie.setPath("/"); //쿠키는 모든 전역에서 보일수있음
        cookie.setHttpOnly(true); //자바스크립트가 가져갈수없게

        return cookie;
    }
}

package org.boot.dontspike.User;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.boot.dontspike.Exception.InvalidCredentialsException;
import org.boot.dontspike.Exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    @GetMapping("/oauth2/authorization/naver")
    public void redirectToNaverLogin(HttpServletResponse response) throws IOException {
        // Naver OAuth2 로그인으로 리다이렉트
        response.sendRedirect("/oauth2/authorization/naver"); // Spring Security가 설정한 리다이렉트 URL로 이동
    }

    @GetMapping("/oauth2/authorization/google")
    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        // Google OAuth2 로그인으로 리다이렉트
        response.sendRedirect("/oauth2/authorization/google"); // Spring Security가 설정한 리다이렉트 URL로 이동
    }
}

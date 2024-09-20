package org.boot.dontspike.OAuth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.boot.dontspike.JWT.JWTUtil;
import org.boot.dontspike.User.User;
import org.boot.dontspike.User.UserRepository;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TokenController {

    @GetMapping("/getAccessToken")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 accessToken 찾기
        String accessToken = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        // accessToken이 없다면 400 Bad Request 응답
        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Access token not found in cookies");
        }


        // AccessToken을 응답으로 반환
        return ResponseEntity.ok(accessToken);
    }
}

package org.boot.dontspike.User;

import lombok.RequiredArgsConstructor;
import org.boot.dontspike.Exception.InvalidCredentialsException;
import org.boot.dontspike.Exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/createuser")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Map<String, String> createUser) {
        String username = createUser.get("username");
        String password = createUser.get("password");
        try {
            userService.createUser(username, password);
            Map<String, String> response = new HashMap<>();
            response.put("message", "회원가입 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UsernameAlreadyExistsException e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }
    @PostMapping("/api/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            User user = userService.login(username, password);
            Map<String, Object> response = new HashMap<>();
            response.put("user_id", user.getId());
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

}

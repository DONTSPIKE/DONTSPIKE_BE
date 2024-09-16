package org.boot.dontspike.User;

import lombok.RequiredArgsConstructor;
import org.boot.dontspike.Exception.InvalidCredentialsException;
import org.boot.dontspike.Exception.UsernameAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    @GetMapping("/api/login")
    public ResponseEntity<String> loginPage() {
        return ResponseEntity.ok("로그인 성공");
    }

}

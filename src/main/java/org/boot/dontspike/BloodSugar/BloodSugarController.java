package org.boot.dontspike.BloodSugar;

import org.boot.dontspike.JWT.JWTUtil;
import org.boot.dontspike.User.User;
import org.boot.dontspike.User.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BloodSugarController {

    private final BloodSugarService bloodSugarService;
    private final org.boot.dontspike.OpenAI.gptService gptService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository; // 유저 레포지토리 추가

    // 아침 공복 혈당 그래프 조회
    @GetMapping("/api/blood-sugar/food")
    public ResponseEntity<List<GraphDto>> getGraph(@RequestHeader("Authorization") String token) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            String username = jwtUtil.getUsername(tokenValue);

            // username 확인
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body(null); // 유저가 없을 때 404 응답
            }

            return ResponseEntity.ok(bloodSugarService.getGraph(username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 월별 혈당 평균값 조회
    @GetMapping("/api/blood-sugar/average")
    public Map<String, Object> getMonthlyAverages(@RequestHeader("Authorization") String token, @RequestParam("year") int year) {
        String tokenValue = token.replace("Bearer ", "");
        String username = jwtUtil.getUsername(tokenValue);

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Map<String, Double> averages = bloodSugarService.getMonthlyAverages(username, year);
        return Map.of(
                "username", username,
                "year", year,
                "monthly_averages", averages
        );
    }

    // 혈당값 기록
    @PostMapping("/api/blood-sugar")
    public ResponseEntity<?> createBloodsugar(@RequestHeader("Authorization") String token, @RequestParam("date") String date, @RequestParam("bloodsugar") Double bloodSugar) {
        try {
            String tokenValue = token.replace("Bearer ", "");
            String username = jwtUtil.getUsername(tokenValue);

            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404).body("User not found");
            }

            LocalDateTime recorddate = LocalDateTime.parse(date);
            bloodSugarService.createOrUpdateBloodSugar(user, recorddate, bloodSugar);
            return ResponseEntity.ok("등록되었습니다.");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("올바른 회원 아이디가 아닙니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 에러: " + e.getMessage());
        }
    }
}

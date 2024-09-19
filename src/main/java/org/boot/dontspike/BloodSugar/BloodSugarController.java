package org.boot.dontspike.BloodSugar;

import org.boot.dontspike.JWT.JWTUtil;
import org.boot.dontspike.User.User;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController//json파일 보낼때 사용하는 annotation
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BloodSugarController {

    private final BloodSugarService bloodSugarService;
    private final org.boot.dontspike.OpenAI.gptService gptService;
    private final JWTUtil jwtUtil;

    //아침 공복 혈당 그래프 조회
    @GetMapping("/api/blood-sugar/food")
    public ResponseEntity<List<GraphDto>> getGraph(@RequestHeader("Authorization") String token) {
        try {
            // JWT 토큰에서 "Bearer " 부분 제거
            String tokenValue = token.replace("Bearer ", "");

            // JWT 토큰에서 user_id를 추출 (JWTUtil을 사용하여 처리)
            String username = jwtUtil.getUsername(tokenValue);

            // 추출된 user_id를 사용하여 그래프 데이터를 가져옴
            return ResponseEntity.ok(bloodSugarService.getGraph(username));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 예외 발생 시 처리
        }
    }

    //월별 혈당 평균값 조회
    @GetMapping("/api/blood-sugar/average")
    public Map<String, Object> getMonthlyAverages(
            @RequestHeader("Authorization") String token,
            @RequestParam("year") int year) {

        // JWT 토큰에서 "Bearer " 부분 제거
        String tokenValue = token.replace("Bearer ", "");

        // JWT 토큰에서 user_id를 추출 (JWTUtil을 사용하여 처리)
        String username = jwtUtil.getUsername(tokenValue);

        Map<String, Double> averages = bloodSugarService.getMonthlyAverages(username, year);
        return Map.of(
                "username", username,
                "year", year,
                "monthly_averages", averages
        );
    }

    //혈당값 기록
    @PostMapping("/api/blood-sugar")
    public ResponseEntity<?> createBloodsugar(@RequestHeader("Authorization") String token, @RequestParam("date") String date, @RequestParam("bloodsugar") Double bloodSugar) {
        try {
            // JWT 토큰에서 "Bearer " 부분 제거
            String tokenValue = token.replace("Bearer ", "");

            // JWT 토큰에서 user_id를 추출 (JWTUtil을 사용하여 처리)
            String username = jwtUtil.getUsername(tokenValue);
            LocalDateTime recorddate = LocalDateTime.parse(date);
            User user = new User();
            user.setUsername(username);
            bloodSugarService.createOrUpdateBloodSugar(user, recorddate, bloodSugar);
            return ResponseEntity.ok("등록되었습니다.");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("올바른 회원 아이디가 아닙니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 에러"+ e.getMessage());
        }
    }

}
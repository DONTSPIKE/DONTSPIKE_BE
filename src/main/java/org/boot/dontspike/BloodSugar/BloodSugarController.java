package org.boot.dontspike.BloodSugar;

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

    //아침 공복 혈당 그래프 조회
    @GetMapping("/api/blood-sugar/food/{user_id}")
    public ResponseEntity<List<GraphDto>> getGraph(@PathVariable String user_id) {
        try {
            Long userIdLong = Long.parseLong(user_id);
            return ResponseEntity.ok(bloodSugarService.getGraph(userIdLong));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //월별 혈당 평균값 조회
    @GetMapping("/api/blood-sugar/average")
    public Map<String, Object> getMonthlyAverages(
            @RequestParam("user_id") Long userId,
            @RequestParam("year") int year) {
        Map<String, Double> averages = bloodSugarService.getMonthlyAverages(userId, year);
        return Map.of(
                "user_id", userId,
                "year", year,
        );
    }

    //혈당값 기록
    @PostMapping("/api/{user_id}/blood-sugar")
    public ResponseEntity<?> createBloodsugar(@PathVariable String user_id, @RequestParam("date") String date, @RequestParam("bloodsugar") Double bloodSugar) {
        try {
            Long userIdLong = Long.parseLong(user_id);
            LocalDateTime recorddate = LocalDateTime.parse(date);
            User user = new User();
            user.setId(userIdLong);
            bloodSugarService.createOrUpdateBloodSugar(user, recorddate, bloodSugar);
            return ResponseEntity.ok("등록되었습니다.");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("올바른 회원 아이디가 아닙니다.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 에러"+ e.getMessage());
        }
    }

}
package org.boot.dontspike.BloodSugar;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController//json파일 보낼때 사용하는 annotation
@RequiredArgsConstructor
@CrossOrigin
public class BloodSugarController {

    private final BloodSugarService bloodSugarService;

    @GetMapping("/api/blood-sugar/food/{user_id}")
    public ResponseEntity<List<GraphDto>> getGraph(@PathVariable String user_id) {
        try {
            Long userIdLong = Long.parseLong(user_id);
            return ResponseEntity.ok(bloodSugarService.getGraph(userIdLong));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("api/blood-sugar/average")
    public Map<String, Object> getMonthlyAverages(
            @RequestParam("user_id") Long userId,
            @RequestParam("year") int year) {
        Map<String, Double> averages = bloodSugarService.getMonthlyAverages(userId, year);
        return Map.of(
                "user_id", userId,
                "year", year,
                "monthly_averages", averages
        );
    }

}
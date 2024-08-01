package org.boot.dontspike.BloodSugar;

import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController//json파일 보낼때 사용하는 annotation
@RequiredArgsConstructor
@CrossOrigin(origins="https://localhost:5173")
public class BloodSugarController {

    private final BloodSugarService bloodSugarService;

    @GetMapping("/api/blood-sugar/food/{user_id}")
    public List<GraphDto> getGraph(@PathVariable Long user_id) {
        return bloodSugarService.getGraph(user_id);
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
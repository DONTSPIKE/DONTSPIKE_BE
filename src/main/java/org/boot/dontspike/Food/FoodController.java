package org.boot.dontspike.Food;

import lombok.Getter;
import org.boot.dontspike.DTO.FoodDto;
import org.boot.dontspike.DTO.FrequentFoodDto;
import org.boot.dontspike.JWT.JWTUtil;
import org.boot.dontspike.User.User;
import org.boot.dontspike.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class FoodController {

    @Autowired
    private FoodService foodService;
    @Autowired
    private org.boot.dontspike.OpenAI.gptService gptService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository; // Add UserRepository to verify users

    public FoodController(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/api/food") //음식 검색-> 음식이름 받으면 그 음식에 대한 정보 출력
    public List<FoodDto> getFood(@RequestParam("search_food") String name) {
        return foodService.getAllFood(name);
    }

    @PostMapping("/api/food") // 음식 직접 등록하기 -> 음식이름 받아서 food 엔티티에 추가(food에 이미 있으면 exception)
    public ResponseEntity<String> createFood(@RequestBody Map<String, String> foodRequest) {
        String foodname = foodRequest.get("foodname");
        Food createdFood = foodService.createFood(foodname);
        return new ResponseEntity<>("등록 되었습니다", HttpStatus.CREATED);
    }

    @PostMapping("/api/diet/add-food") //음식 추가하기-> foodId랑 date 받고, 날짜별 음식 기록하기
    public ResponseEntity<String> addFoodToBloodSugarRecord(
            @RequestHeader("Authorization") String token,
            @RequestParam("foodId") Long foodId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {
        try {
            // JWT 토큰에서 "Bearer " 부분 제거
            String tokenValue = token.replace("Bearer ", "");

            // JWT 토큰에서 username 추출
            String username = jwtUtil.getUsername(tokenValue);

            // username 존재 확인
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // 음식 추가
            foodService.addFoodToBloodSugarRecord(username, foodId, recordDate);
            return ResponseEntity.ok("음식이 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생: " + e.getMessage());
        }
    }

    @GetMapping("/api/food/favorites") // 최근 30일간 자주먹은음식 조회
    public ResponseEntity<Map<String, Object>> getFoodsEatenAtLeastFiveTimesInMonth(
            @RequestHeader("Authorization") String token,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        try {
            startDate = LocalDateTime.now().minusDays(30);
            endDate = LocalDateTime.now();

            // JWT 토큰에서 "Bearer " 부분 제거
            String tokenValue = token.replace("Bearer ", "");

            // JWT 토큰에서 username 추출
            String username = jwtUtil.getUsername(tokenValue);

            // username 존재 확인
            User user = userRepository.findByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 유저가 없을 때 404 응답
            }

            // 음식 정보 및 GPT 분석 가져오기
            List<FrequentFoodDto> frequentFoods = foodService.getFoodsEatenAtLeastFiveTimesInMonth(username, startDate, endDate);
            FrequentAnalysisDto analysisDto = gptService.getFrequentAnalysis(username, startDate, endDate);

            Map<String, Object> response = new HashMap<>();
            response.put("frequentFoods", frequentFoods);
            response.put("analysisDto", analysisDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "서버 에러: " + e.getMessage()));
        }
    }
}

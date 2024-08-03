package org.boot.dontspike.Food;

import lombok.Getter;
import org.boot.dontspike.DTO.FoodDto;

//import org.boot.dontspike.DTO.FrequentFoodDto;
import org.boot.dontspike.DTO.FrequentFoodDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/api/food") //음식 검색-> 음식이름 받으면 그 음식에 대한 정보 출력
    public List<FoodDto> getFood(@RequestParam("search_food")String name) {
        return foodService.getAllFood(name);
    }

    @PostMapping("/api/food") //음식 직접 등록하기 -> 음식이름 받아서 food 엔티티에 추가(food에 이미 있으면 exception)
    public ResponseEntity<String> createFood(@RequestBody String foodname) {
        Food createdFood = foodService.createFood(foodname);
        return new ResponseEntity<>("등록 되었습니다", HttpStatus.CREATED);
        //Service에서 이미 등록된 음식입니다 처리
    }

    @PostMapping("/api/diet/add-food") //음식 추가하기-> foodId랑 date 받고, 날짜별 음식 기록하기
    public void addFoodToBloodSugarRecord(
            @RequestParam Long userId,
            @RequestParam Long foodId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {
        foodService.addFoodToBloodSugarRecord(userId, foodId, recordDate);
    }

    @GetMapping("/api/food/favorites") // 자주먹은음식 조회 -> 달 입력 받아서 리스트로 자주먹은음식이름이 responsedata
    public ResponseEntity<List<FrequentFoodDto>> getFoodsEatenAtLeastFiveTimesInMonth(@RequestParam("month") String month) {
        LocalDate monthDate = LocalDate.parse(month + "-01"); // "YYYY-MM" 형식으로 입력 받음
        List<FrequentFoodDto> frequentFoods = foodService.getFoodsEatenAtLeastFiveTimesInMonth(monthDate);
        return ResponseEntity.ok(frequentFoods);
    }
}

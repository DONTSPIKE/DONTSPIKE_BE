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
import java.time.format.DateTimeFormatter;
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

    @PostMapping("/api/food") // 음식 직접 등록하기 -> 음식이름 받아서 food 엔티티에 추가(food에 이미 있으면 exception)
    public ResponseEntity<String> createFood(@RequestBody Map<String, String> foodRequest) {
        String foodname = foodRequest.get("foodname");
        Food createdFood = foodService.createFood(foodname);
        return new ResponseEntity<>("등록 되었습니다", HttpStatus.CREATED);
    }
//    @PostMapping("/api/food") //음식 직접 등록하기 -> 음식이름 받아서 food 엔티티에 추가(food에 이미 있으면 exception)
//    public ResponseEntity<String> createFood(@RequestBody String foodname) {
//        Food createdFood = foodService.createFood(foodname);
//        return new ResponseEntity<>("등록 되었습니다", HttpStatus.CREATED);
//        //Service에서 이미 등록된 음식입니다 처리
//    }

    @PostMapping("/api/diet/add-food") //음식 추가하기-> foodId랑 date 받고, 날짜별 음식 기록하기
    public void addFoodToBloodSugarRecord(
            @RequestParam ("userId")Long userId,
            @RequestParam ("foodId")Long foodId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate recordDate) {
        foodService.addFoodToBloodSugarRecord(userId, foodId, recordDate);
    }


    @GetMapping("/api/food/favorites")
    public ResponseEntity<List<FrequentFoodDto>> getFoodsEatenAtLeastFiveTimesInMonth(
        @RequestParam Long userId,
        @RequestParam String startDate,
        @RequestParam String endDate) {

    // String으로 받은 날짜를 LocalDateTime으로 변환
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    LocalDateTime start = LocalDateTime.parse(startDate, formatter);
    LocalDateTime end = LocalDateTime.parse(endDate, formatter);

    List<FrequentFoodDto> frequentFoods = foodService.getFoodsEatenAtLeastFiveTimesInMonth(userId, start, end);
    return ResponseEntity.ok(frequentFoods);
}
}

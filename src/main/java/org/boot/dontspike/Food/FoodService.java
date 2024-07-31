package org.boot.dontspike.Food;

import org.boot.dontspike.DTO.FoodDto;
import org.boot.dontspike.FoodWiki.FoodWikiRepository;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.boot.dontspike.FoodWiki.Foodwiki;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FoodWikiRepository foodWikiRepository;
    @Autowired
    private FoodRecordRepository foodRecordRepository;

    public List<FoodDto> getAllFood(String foodname) {
        List<Food> foods = foodRepository.findByFoodnameContaining(foodname);
        List<FoodDto> foodDtos = new ArrayList<>();

        for (Food food : foods) {
            FoodDto foodDto = new FoodDto();
            foodDto.setFoodname(food.getFoodname());
            foodDto.setFat(food.getFat());
            foodDto.setProtein(food.getProtein());
            foodDto.setCarbohydrate(food.getCarbohydrate());
            foodDto.setSodium(food.getSodium());
            foodDto.setCholesterol(food.getCholesterol());
            foodDto.setCalorie(food.getCalorie());

            foodDtos.add(foodDto);
        }
        return foodDtos;

    }

    public List<FoodDetailDto> searchFoodDetailsByName(String foodname) {
        List<Food> foods = foodRepository.findByFoodnameContaining(foodname);
        List<FoodDetailDto> foodDetailDtos = new ArrayList<>();

        for (Food food : foods) {
            Foodwiki foodwiki = foodWikiRepository.findByFood(food);

            if (foodwiki != null) {
                FoodDetailDto dto = new FoodDetailDto();
                dto.setFoodname(food.getFoodname());
                dto.setAmount(food.getAmount());
                dto.setCalorie(food.getCalorie());
                dto.setProtein(food.getProtein());
                dto.setFat(food.getFat());
                dto.setSodium(food.getSodium());
                dto.setCholesterol(food.getCholesterol());
                dto.setCarbohydrate(food.getCarbohydrate());
                dto.setExpertOpinion(foodwiki.getExpertOpinion());
                dto.setProperIntake(foodwiki.getProperIntake());
                dto.setIngestionMethod(foodwiki.getIngestionMethod());
                dto.setGI(foodwiki.getGi());

                foodDetailDtos.add(dto);
            }
        }
        return foodDetailDtos;
    }
    public Food createFood(String foodname) {
        Optional<Food> existingFood = foodRepository.findByFoodname(foodname);
        if (existingFood.isPresent()) {
            throw new FoodAlreadyExistsException("이미 등록되어있는 음식입니다.");
        } else {
            Food newFood = new Food();
            newFood.setFoodname(foodname);
            return foodRepository.save(newFood);
        }
    }
    @Transactional
    public void addFoodRecord(Long foodId, LocalDate recordDate){
        Food food=foodRepository.findById(foodId)
                .orElseThrow(()->new RuntimeException("Food not found"));

        FoodRecord foodRecord=new FoodRecord();
        foodRecord.setFood(food);
        foodRecord.setRecorddate(recordDate);

        foodRecordRepository.save(foodRecord);
    }

    public List<String> getFoodsByDate(LocalDate recordDate) {
        List<FoodRecord> foodRecords = foodRecordRepository.findByRecorddate(recordDate);
        return foodRecords.stream()
                .map(foodRecord -> foodRecord.getFood().getFoodname())
                .collect(Collectors.toList());
    }

    public List<String> getFoodsEatenAtLeastFiveTimesInMonth(LocalDate monthDate) {
        LocalDate startDate = monthDate.withDayOfMonth(1);
        LocalDate endDate = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
        return foodRecordRepository.findFrequentFoodNames(startDate, endDate);
}

}



package org.boot.dontspike.Food;

import org.boot.dontspike.BloodSugar.BloodSugar;
import org.boot.dontspike.BloodSugar.BloodSugarRepository;
import org.boot.dontspike.BloodSugar.FoodBloodSugarMapping;
import org.boot.dontspike.BloodSugar.FoodBloodSugarMappingRepository;
import org.boot.dontspike.DTO.FoodDto;
import org.boot.dontspike.DTO.FrequentFoodDto;
import org.boot.dontspike.FoodWiki.FoodWikiRepository;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.boot.dontspike.FoodWiki.Foodwiki;
import org.boot.dontspike.User.User;
import org.boot.dontspike.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FoodWikiRepository foodWikiRepository;


    @Autowired
    private BloodSugarRepository bloodSugarRepository;
    @Autowired
    private FoodBloodSugarMappingRepository FoodbloodSugarmappingRepository;
    @Autowired
    private FoodBloodSugarMappingRepository foodBloodSugarMappingRepository;

    @Autowired
    private UserRepository userRepository;

    public List<FoodDto> getAllFood(String foodname) {
        List<Food> foods = foodRepository.findByFoodnameContaining(foodname);
        List<FoodDto> foodDtos = new ArrayList<>();

        for (Food food : foods) {
            FoodDto foodDto = new FoodDto();
            foodDto.setFoodId(Math.toIntExact(food.getFooddata_id()));
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
//    public List<FoodDto> getFoodsByBloodSugarDate(LocalDate recordDate) {
//        LocalDateTime startOfDay = recordDate.atStartOfDay();
//        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
//
//        // BloodSugar 레코드를 날짜로 찾기
//        List<BloodSugar> bloodSugars = bloodSugarRepository.findByUserIdAndDateRange(
//                /* 사용자 ID를 제공하는 방법에 따라 변경 */
//                1L,
//                startOfDay,
//                endOfDay
//        );
//
//        if (bloodSugars.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        // 해당 날짜에 추가된 음식들을 조회
//        List<Food> foods = foodBloodSugarMappingRepository.findFoodsByBloodSugarRecordDate(startOfDay);
//
//        // DTO로 변환
//        return foods.stream()
//                .map(food -> new FoodDto(food))
//                .collect(Collectors.toList());
//    }
@Transactional
public void addFoodToBloodSugarRecord(Long userId, Long foodId, LocalDate recordDate) {
    // 1. Food 엔티티 가져오기
    Food food = foodRepository.findById(foodId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid food ID"));

    // 2. 현재 사용자 가져오기
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

    // 3. 해당 날짜와 사용자에 대한 BloodSugar 기록 찾기
    LocalDateTime startOfDay = recordDate.atStartOfDay();
    LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1); // 하루의 마지막 순간까지

    Optional<BloodSugar> optionalBloodSugar = bloodSugarRepository.findFirstByUserAndRecordDateBetween(user, startOfDay, endOfDay);

    if (optionalBloodSugar.isEmpty()) {
        throw new IllegalArgumentException("No blood sugar record found for the given date");
    }

    BloodSugar bloodSugarRecord = optionalBloodSugar.get();

    // 4. FoodBloodSugarMapping 생성 및 저장
    FoodBloodSugarMapping mapping = new FoodBloodSugarMapping();
    mapping.setBloodSugarRecordId(bloodSugarRecord);
    mapping.setFoodDataId(food);

    foodBloodSugarMappingRepository.save(mapping);
}

}



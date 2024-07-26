package org.boot.dontspike.Food;

import org.boot.dontspike.FoodWiki.FoodWikiRepository;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.boot.dontspike.FoodWiki.Foodwiki;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FoodWikiRepository foodWikiRepository;

    public List<FoodDetailDto> searchFoodDetailsByName(String foodname) {
        List<Food> foods = foodRepository.findByFoodnameContaining(foodname);
        return foods.stream().map(this::convertToFoodDetailDto).collect(Collectors.toList());
    }

    private FoodDetailDto convertToFoodDetailDto(Food food) {
        FoodDetailDto dto = new FoodDetailDto();
        dto.setFoodname(food.getFoodname());
        dto.setAmount(food.getAmount());
        dto.setCalorie(food.getCalorie());
        dto.setProtein(food.getProtein());
        dto.setFat(food.getFat());
        dto.setSodium(food.getSodium());
        dto.setCholesterol(food.getCholesterol());
        dto.setCarbohydrate(food.getCarbohydrate());
        dto.setFoodImage(food.getFoodImage());

        List<Foodwiki> foodWikis = foodWikiRepository.findByFooddataId((long) food.getFooddata_id());
        if (!foodWikis.isEmpty()) {
            Foodwiki foodWiki = foodWikis.get(0);
            dto.setExpertOpinion(foodWiki.getExpertOpinion());
            dto.setProperIntake(foodWiki.getProperIntake());
            dto.setIngestionMethod(foodWiki.getIngestionMethod());
            dto.setGI(foodWiki.getGi());
        }

        return dto;
    }
}
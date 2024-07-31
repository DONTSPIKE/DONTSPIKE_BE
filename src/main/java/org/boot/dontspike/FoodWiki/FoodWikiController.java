package org.boot.dontspike.FoodWiki;

import org.boot.dontspike.Food.FoodService;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FoodWikiController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/api/foodwiki") //푸드위키 음식 검색 -> 음식 입력 받으면 음식 정보랑 전문가 의견, 섭취 방법 등이 출력
    public List<FoodDetailDto> getFoodWiki(@RequestParam("search_food") String name) {
        return foodService.searchFoodDetailsByName(name); //FoodDetailDto-> food랑 foodwiki 데이터가 합쳐진 애가 return
    }
}

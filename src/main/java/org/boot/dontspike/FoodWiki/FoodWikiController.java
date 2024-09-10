package org.boot.dontspike.FoodWiki;

import org.boot.dontspike.Food.FoodService;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class FoodWikiController {
    @Autowired
    private FoodService foodService;
    @Autowired
    private org.boot.dontspike.OpenAI.gptService gptService;

//    @GetMapping("/api/foodwiki") //푸드위키 음식 검색 -> 음식 입력 받으면 음식 정보랑 전문가 의견, 섭취 방법 등이 출력
//    public List<FoodDetailDto> getFoodWiki(@RequestParam("search_food") String name) {
//        return gptService.getFoodDetails(name);//FoodDetailDto-> food랑 foodwiki 데이터가 합쳐진 애가 return
//    }
@GetMapping("/api/foodwiki")
public List<FoodDetailDto> getFoodWiki(@RequestParam("search_food") String name) {
    // 음식 이름에 대한 정보를 가져옴
    List<FoodDetailDto> foodDetails = gptService.getFoodDetails(name);

    // 음식 이름에 맞는 데이터만 필터링
    return foodDetails.stream()
            .filter(food -> food.getFoodname().equalsIgnoreCase(name))
            .collect(Collectors.toList());
}
}

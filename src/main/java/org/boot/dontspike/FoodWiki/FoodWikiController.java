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

    @GetMapping("/api/foodwiki") //푸드위키 음식 검색 -> 음식 입력 받으면 음식 정보랑 전문가 의견, 섭취 방법 등이 출력
    public FoodDetailDto getFoodWiki(@RequestParam("search_food") String name) {
        return gptService.getFoodDetails(name);//FoodDetailDto-> food랑 foodwiki 데이터가 합쳐진 애가 return
    }
}

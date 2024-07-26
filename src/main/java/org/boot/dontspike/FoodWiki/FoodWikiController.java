package org.boot.dontspike.FoodWiki;

import org.boot.dontspike.Food.FoodService;
import org.boot.dontspike.DTO.FoodDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FoodWikiController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/search")
    public List<FoodDetailDto> searchFoodDetailsByName(@RequestParam String name) {
        return foodService.searchFoodDetailsByName(name); //FoodDetailDto-> food랑 foodwiki 데이터가 합쳐진 애가 return
    }
}

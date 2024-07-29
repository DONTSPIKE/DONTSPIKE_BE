package org.boot.dontspike.Food;

import org.boot.dontspike.DTO.FoodDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class FoodController {
    @Autowired
    private FoodService foodService;

    @GetMapping("/food")
    public ResponseEntity<List<FoodDto>> getAllFood(){
        List<FoodDto> foodList=foodService.getAllFood();
        if(foodList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(foodList, HttpStatus.OK);
    }

}

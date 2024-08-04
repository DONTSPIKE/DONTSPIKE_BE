package org.boot.dontspike.DTO;

import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.Food.Food;

@Getter @Setter
public class FoodDto {
    private String foodname;
    private Integer foodId;
    private Double calorie;
    private Double protein;
    private Double fat;
    private Double sodium;
    private Double cholesterol;
    private Double carbohydrate;

    public FoodDto(Food food) {
    }

    public FoodDto() {

    }
}

package org.boot.dontspike.DTO;

import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.Food.Food;

@Getter @Setter
public class FoodDto {
    private String foodname;
    private int foodId;
    private double calorie;
    private double protein;
    private double fat;
    private double sodium;
    private double cholesterol;
    private double carbohydrate;

    public FoodDto(Food food) {
    }

    public FoodDto() {

    }
}

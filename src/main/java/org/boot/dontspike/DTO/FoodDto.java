package org.boot.dontspike.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FoodDto {
    private String foodname;
    private double calorie;
    private double protein;
    private double fat;
    private double sodium;
    private double cholesterol;
    private double carbohydrate;
}


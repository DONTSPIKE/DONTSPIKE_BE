package org.boot.dontspike.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FoodDto {
    private String foodname;
    private int calorie;
    private int protein;
    private int fat;
    private int sodium;
    private int cholesterol;
    private int carbohydrate;
    private String foodImage;
}


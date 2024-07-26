package org.boot.dontspike.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodDetailDto {
    private String foodname;
    private int amount;
    private int calorie;
    private int protein;
    private int fat;
    private int sodium;
    private int cholesterol;
    private int carbohydrate;
    private String foodImage;
    private String expertOpinion;
    private String properIntake;
    private String ingestionMethod;
    private double GI;
}

package org.boot.dontspike.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodDetailDto {
//    private int fooddata_id;
    private String foodname;
    private int amount;
    private double calorie;
    private double protein;
    private double fat;
    private double sodium;
    private double cholesterol;
    private double carbohydrate;

    private String expertOpinion;
    private String properIntake;
    private String ingestionMethod;
    private String GI;
}

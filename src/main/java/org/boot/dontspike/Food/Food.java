package org.boot.dontspike.Food;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fooddata_id;

    @Column(name="foodname",length=50,nullable = false)
    private String foodname;

    @Column(name="amount")
    private int amount;

    @Column(name="calorie")
    private int calorie;

    @Column(name = "protein")
    private int protein;

    @Column(name = "fat")
    private int fat;

    @Column(name = "sodium")
    private int sodium;

    @Column(name = "cholesterol")
    private int cholesterol;

    @Column(name = "carbohydrate")
    private int carbohydrate;

    @Column(name = "food_image", columnDefinition = "TEXT")
    private String foodImage;



}

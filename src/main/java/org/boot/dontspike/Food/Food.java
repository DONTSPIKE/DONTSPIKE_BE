package org.boot.dontspike.Food;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.FoodWiki.Foodwiki;

@Getter
@Setter
@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fooddata_id;

    @OneToOne(mappedBy = "food")
    private Foodwiki foodwiki;

    @Column(name="foodname",length=50,nullable = false)
    private String foodname;

    @Column(name="amount")
    private int amount;

    @Column(name="calorie")
    private double calorie;

    @Column(name = "protein")
    private double protein;

    @Column(name = "fat")
    private double fat;

    @Column(name = "sodium")
    private double sodium;

    @Column(name = "cholesterol")
    private double cholesterol;

    @Column(name = "carbohydrate")
    private double carbohydrate;



}

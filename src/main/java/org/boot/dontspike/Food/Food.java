package org.boot.dontspike.Food;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.BloodSugar.FoodBloodSugarMapping;
import org.boot.dontspike.FoodWiki.Foodwiki;

import java.util.List;

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
    private Integer amount;

    @Column(name="calorie")
    private Double calorie;

    @Column(name = "protein")
    private Double protein;

    @Column(name = "fat")
    private Double fat;

    @Column(name = "sodium")
    private Double sodium;

    @Column(name = "cholesterol")
    private Double cholesterol;

    @Column(name = "carbohydrate")
    private Double carbohydrate;

    @OneToMany(mappedBy = "food")
    private List<FoodBloodSugarMapping> foodBloodSugarMappings;



}

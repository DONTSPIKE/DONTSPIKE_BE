package org.boot.dontspike.Food;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class FoodRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private LocalDate recorddate;

}

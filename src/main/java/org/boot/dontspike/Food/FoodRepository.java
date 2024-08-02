package org.boot.dontspike.Food;

import org.boot.dontspike.DTO.FrequentFoodDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByFoodnameContaining(String foodname);

    Optional<Food> findByFoodname(String foodname);
}

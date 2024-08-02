package org.boot.dontspike.Food;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByFoodnameContaining(String foodname);

    Optional<Food> findByFoodname(String foodname);

}

package org.boot.dontspike.FoodWiki;

import org.boot.dontspike.Food.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodWikiRepository extends JpaRepository<Foodwiki, Long> {
    List<Foodwiki> findByFooddataId(Long fooddataId);

    Foodwiki findByFood(Food food);
}

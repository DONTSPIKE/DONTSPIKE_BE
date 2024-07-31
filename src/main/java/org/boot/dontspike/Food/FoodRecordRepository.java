package org.boot.dontspike.Food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FoodRecordRepository extends JpaRepository<FoodRecord, Long> {
    List<FoodRecord> findByRecorddate(LocalDate recorddate);
    @Query("SELECT f.food.foodname FROM FoodRecord f WHERE f.recorddate >= :startDate AND f.recorddate <= :endDate GROUP BY f.food.foodname HAVING COUNT(f.food.fooddata_id) >= 5")
    List<String> findFrequentFoodNames(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

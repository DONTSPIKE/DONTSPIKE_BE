package org.boot.dontspike.Food;

import org.boot.dontspike.DTO.FrequentFoodDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FoodRecordRepository extends JpaRepository<FoodRecord, Long> {
    List<FoodRecord> findByRecorddate(LocalDate recorddate);
    @Query("SELECT new org.boot.dontspike.DTO.FrequentFoodDto(f.food.foodname, COUNT(f), f.food.fooddata_id) " +
            "FROM FoodRecord f " +
            "WHERE f.recorddate >= :startDate AND f.recorddate <= :endDate " +
            "GROUP BY f.food.foodname, f.food.fooddata_id " +
            "HAVING COUNT(f) >= 5")
    List<FrequentFoodDto> findFrequentFoodNames(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

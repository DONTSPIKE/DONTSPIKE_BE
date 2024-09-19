package org.boot.dontspike.Food;

import org.boot.dontspike.DTO.FrequentFoodDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findByFoodnameContaining(String foodname);

    Optional<Food> findByFoodname(String foodname);


    @Query("SELECT new org.boot.dontspike.DTO.FrequentFoodDto(f.foodDataId.fooddata_id, f.foodDataId.foodname, COUNT(f)) " +
            "FROM FoodBloodSugarMapping f " +
            "WHERE f.bloodSugarRecordId.user.username = :username AND f.bloodSugarRecordId.recordDate >= :startDate AND f.bloodSugarRecordId.recordDate < :endDate " +
            "GROUP BY f.foodDataId.fooddata_id, f.foodDataId.foodname " +
            "HAVING COUNT(f) >= 5")
    List<FrequentFoodDto> findFoodsEatenAtLeastFiveTimes(
            @Param("username") String username,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
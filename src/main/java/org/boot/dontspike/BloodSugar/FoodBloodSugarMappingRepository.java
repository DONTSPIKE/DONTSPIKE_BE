package org.boot.dontspike.BloodSugar;

import org.boot.dontspike.Food.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FoodBloodSugarMappingRepository extends JpaRepository<FoodBloodSugarMapping, Long> {
    @Query("SELECT f FROM FoodBloodSugarMapping f " +
            "WHERE f.bloodSugarRecordId.recordDate = :recordDate AND f.foodDataId.fooddata_id = :foodId")
    Optional<FoodBloodSugarMapping> findMappingByDateAndFoodId(@Param("recordDate") LocalDateTime recordDate, @Param("foodId") Long foodId);

    @Modifying
    @Query("DELETE FROM FoodBloodSugarMapping f " +
            "WHERE f.bloodSugarRecordId.recordDate = :recordDate AND f.foodDataId.fooddata_id = :foodId")
    void deleteMappingByDateAndFoodId(@Param("recordDate") LocalDateTime recordDate, @Param("foodId") Long foodId);
}


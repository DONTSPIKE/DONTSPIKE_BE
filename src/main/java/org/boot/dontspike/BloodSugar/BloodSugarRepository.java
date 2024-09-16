package org.boot.dontspike.BloodSugar;

import org.boot.dontspike.User.User;
import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BloodSugarRepository extends JpaRepository<BloodSugar, Long> {
    List<BloodSugar> findByUserIdAndRecordDateBefore(Long userId, LocalDateTime recordDate);
    @Query("SELECT b FROM BloodSugar b WHERE b.user.id = :userId AND b.recordDate BETWEEN :startDate AND :endDate")
    List<BloodSugar> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Optional<BloodSugar> findFirstByUserAndRecordDateBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    //해당 날짜가 같은 데이터가 있는지 찾는 메서드
    @Query("SELECT b FROM BloodSugar b WHERE b.user.id = :userId AND FUNCTION('DATE_FORMAT', b.recordDate, '%Y-%m-%d') = FUNCTION('DATE_FORMAT', :recordDate, '%Y-%m-%d')")
    Optional<BloodSugar> findByUserIdAndDay(@Param("userId") Long userId, @Param("recordDate") LocalDate recordDate);

//    @Query(value = "SELECT MONTH(record_date) AS month, AVG(value) AS average " +
//            "FROM blood_sugar " +
//            "WHERE user_id = :userId AND YEAR(record_date) = :year " +
//            "GROUP BY MONTH(record_date)", nativeQuery = true)
//    Map<Integer, Double> getMonthlyAverages(@Param("userId") Long userId, @Param("year") int year);
}

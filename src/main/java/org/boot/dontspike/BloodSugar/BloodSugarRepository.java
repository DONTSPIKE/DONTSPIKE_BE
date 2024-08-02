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
import java.util.Optional;

public interface BloodSugarRepository extends JpaRepository<BloodSugar, Long> {
    List<BloodSugar> findByUserIdAndRecordDateAfter(Long userId, LocalDateTime recordDate);
    @Query("SELECT b FROM BloodSugar b WHERE b.user.id = :userId AND b.recordDate BETWEEN :startDate AND :endDate")
    List<BloodSugar> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    Optional<BloodSugar> findByUserAndRecordDate(User user, LocalDateTime recordDate);

    Optional<Object> findByUserAndRecordDateBetween(User currentUser, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Optional<BloodSugar> findFirstByUserAndRecordDateBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);
}

package org.boot.dontspike.BloodSugar;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BloodSugarRepository extends JpaRepository<BloodSugar, Long> {
    List<BloodSugar> findByUserIdAndRecordDateAfter(Long userId, LocalDateTime recordDate);
}
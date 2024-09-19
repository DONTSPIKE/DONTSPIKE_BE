package org.boot.dontspike.BloodSugar;

import org.boot.dontspike.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BloodSugarRepository extends JpaRepository<BloodSugar, Long> {

    // username을 기준으로 recordDate 이전의 데이터 조회
    List<BloodSugar> findByUserUsernameAndRecordDateBefore(String username, LocalDateTime recordDate);

    // username을 기준으로 날짜 범위 내 데이터 조회
    @Query("SELECT b FROM BloodSugar b WHERE b.user.username = :username AND b.recordDate BETWEEN :startDate AND :endDate")
    List<BloodSugar> findByUsernameAndDateRange(@Param("username") String username, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // 특정 유저의 특정 날짜에 기록된 첫 번째 혈당 데이터 조회
    Optional<BloodSugar> findFirstByUserAndRecordDateBetween(User user, LocalDateTime startOfDay, LocalDateTime endOfDay);

    // 해당 날짜에 같은 데이터가 있는지 찾는 메서드 (username 기반)
    @Query("SELECT b FROM BloodSugar b WHERE b.user.username = :username AND FUNCTION('DATE_FORMAT', b.recordDate, '%Y-%m-%d') = FUNCTION('DATE_FORMAT', :recordDate, '%Y-%m-%d')")
    Optional<BloodSugar> findByUsernameAndDay(@Param("username") String username, @Param("recordDate") LocalDate recordDate);

}

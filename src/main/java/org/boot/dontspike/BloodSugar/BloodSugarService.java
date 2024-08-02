package org.boot.dontspike.BloodSugar;

import lombok.RequiredArgsConstructor;
import org.boot.dontspike.Food.Food;
import org.boot.dontspike.Food.FoodAlreadyExistsException;
import org.boot.dontspike.User.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodSugarService {
    private final BloodSugarRepository repository;

    public List<GraphDto> getGraph(Long userId){
        List<BloodSugar> bloodSugarList = repository.findByUserIdAndRecordDateAfter(userId, LocalDateTime.now().minusDays(7));
        return bloodSugarList.stream().map(bloodSugar -> new GraphDto(bloodSugar))
                .collect(Collectors.toList());
    }
    public Map<String, Double> getMonthlyAverages(Long userId, int year) {
        // 연도의 시작과 끝 날짜 계산
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

        // 주어진 사용자와 연도에 해당하는 혈당 기록 조회
        List<BloodSugar> bloodSugarList = repository.findByUserIdAndDateRange(userId, startOfYear, endOfYear);

        // 월별 평균 계산
        Map<String, Double> monthlyAverages = bloodSugarList.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRecordDate().getMonth().name(),
                        Collectors.averagingDouble(BloodSugar::getBloodSugar)
                ));

        // 월 이름을 정렬된 순서로 반환
        Map<String, Double> sortedMonthlyAverages = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            String monthName = LocalDateTime.of(year, month, 1, 0, 0).getMonth().name();
            sortedMonthlyAverages.put(monthName, monthlyAverages.getOrDefault(monthName, 0.0));
        }

        return sortedMonthlyAverages;
    }

    @Transactional
    public void createOrUpdateBloodSugar(User user, LocalDateTime date, Double bloodSugar) {
        BloodSugar bloodSugarRecord = repository.findByUserAndRecordDate(user, date)
                .orElse(new BloodSugar(user, date));
        bloodSugarRecord.setBloodSugar(bloodSugar);
        repository.save(bloodSugarRecord);
    }

}
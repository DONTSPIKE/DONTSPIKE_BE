package org.boot.dontspike.BloodSugar;

import lombok.RequiredArgsConstructor;
import org.boot.dontspike.Food.Food;
import org.boot.dontspike.Food.FoodAlreadyExistsException;
import org.boot.dontspike.User.User;
import org.hibernate.graph.Graph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BloodSugarService {
    private final BloodSugarRepository repository;

    public List<GraphDto> getGraph(Long userId){
        LocalDateTime endDate = LocalDateTime.now().toLocalDate().atStartOfDay().plusDays(1);
        List<BloodSugar> bloodSugarList = repository.findByUserIdAndRecordDateBefore(userId, endDate);
        List<BloodSugar> lastSevenRecords = bloodSugarList.stream()
                .sorted(Comparator.comparing(BloodSugar::getRecordDate).reversed()) // 날짜순으로 정렬
                .limit(7) // 최근 7개 선택
                .collect(Collectors.toList());

        Double expectedBloodsugar = lastSevenRecords.stream()
                .mapToDouble(BloodSugar::getBloodSugar)
                .average()
                .orElse(0);
        GraphDto expectedGraph = new GraphDto(LocalDateTime.now().plusDays(1), expectedBloodsugar);
        List<GraphDto> graphDtoList = bloodSugarList.stream().map(bloodSugar -> new GraphDto(bloodSugar))
                .collect(Collectors.toList());
        graphDtoList.add(expectedGraph);
        return graphDtoList;
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
        LocalDate findDate = date.toLocalDate();
        BloodSugar bloodSugarRecord = repository.findByUserIdAndDay(user.getId(), findDate)
                .orElse(new BloodSugar(user, date));
        bloodSugarRecord.setBloodSugar(bloodSugar);
        repository.save(bloodSugarRecord);
    }

}
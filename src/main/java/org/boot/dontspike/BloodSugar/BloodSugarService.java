package org.boot.dontspike.BloodSugar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        LocalDateTime startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endOfYear = LocalDate.of(year, 12, 31).atTime(23, 59, 59);

        List<BloodSugar> bloodSugarList = repository.findByUserIdAndDateRange(userId, startOfYear, endOfYear);

        Map<String, Double> monthlyAverages = new HashMap<>();

        bloodSugarList.stream()
                .collect(Collectors.groupingBy(
                        b -> b.getRecordDate().getMonth().name(),
                        Collectors.averagingDouble(BloodSugar::getBloodSugar)
                ))
                .forEach((month, average) -> monthlyAverages.put(month, average));

        return monthlyAverages;
    }

}
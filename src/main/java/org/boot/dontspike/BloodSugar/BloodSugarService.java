package org.boot.dontspike.BloodSugar;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
}

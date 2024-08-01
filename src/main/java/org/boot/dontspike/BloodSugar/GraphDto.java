package org.boot.dontspike.BloodSugar;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@JsonSerialize
@JsonDeserialize
@Getter @Setter
@RequiredArgsConstructor
public class GraphDto {

    private LocalDateTime recorddate;
    private double bloodsugar;
    private List<String> foodBsMappingId;

    public GraphDto(BloodSugar bloodSugar) {
        // 1. 각각의 BloodSugar -> dto로 전환.
        this.recorddate = bloodSugar.getRecordDate();
        this.bloodsugar = bloodSugar.getBloodSugar();
        this.foodBsMappingId = bloodSugar.getFoodBloodSugarMappings().stream()
                .map(mapping -> mapping.getFoodDataId().getFoodname())
                .collect(Collectors.toList());
    }
}
package org.boot.dontspike.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrequentFoodDto {

    private Long foodDataId;
    private String foodName;
    private Long count;

    public FrequentFoodDto(Long foodDataId, String foodName, Long count) {
        this.foodDataId = foodDataId;
        this.foodName = foodName;
        this.count = count;
    }

}
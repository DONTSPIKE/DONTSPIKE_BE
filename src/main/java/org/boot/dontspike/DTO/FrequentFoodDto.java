package org.boot.dontspike.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FrequentFoodDto {
    private String foodName;
    private long count;
    private Long foodId;


}

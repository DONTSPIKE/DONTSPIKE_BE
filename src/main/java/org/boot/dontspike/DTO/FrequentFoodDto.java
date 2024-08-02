package org.boot.dontspike.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FrequentFoodDto {
    private String foodName;
    private long count;

    public FrequentFoodDto(String foodName, long count) {
        this.foodName = foodName;
        this.count = count;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

}

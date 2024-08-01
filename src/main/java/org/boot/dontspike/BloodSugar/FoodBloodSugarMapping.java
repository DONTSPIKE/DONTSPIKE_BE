package org.boot.dontspike.BloodSugar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.BloodSugar.BloodSugar;
import org.boot.dontspike.Food.Food;

@Getter
@Setter
@Entity
@Table(name="food_bs_mapping")
public class FoodBloodSugarMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodBsMappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bloodsugarrecord_id", nullable = false)
    private BloodSugar bloodSugarRecordId;

    @JoinColumn(name = "fooddata_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Food foodDataId;
}

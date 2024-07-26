package org.boot.dontspike.BloodSugar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="food_bs_mapping")
public class FoodBloodSugarMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodBsMappingId;

    @Column(name = "bloodsugarrecord_id", nullable = false)
    private Long bloodSugarRecordId;

    @Column(name = "fooddata_id", nullable = false)
    private Long foodDataId;

    @Column(name = "bloodsugar", nullable = false)
    private double bloodSugar;
}

package org.boot.dontspike.BloodSugar;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="blood_sugar_record")
public class BloodSugar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bloodsugarrecordId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "recorddate", nullable = false)
    private LocalDateTime recordDate;
}

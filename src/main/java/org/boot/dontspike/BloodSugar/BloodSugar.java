package org.boot.dontspike.BloodSugar;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.boot.dontspike.User.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@Setter
@Entity
@Table(name="blood_sugar_record")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class BloodSugar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bloodsugarRecordId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn (name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime recordDate;

    @OneToMany(mappedBy = "bloodSugarRecordId")
    private List<FoodBloodSugarMapping> foodBloodSugarMappings = new ArrayList<>();

    @Column(name = "bloodsugar", nullable = false)
    private double bloodSugar;
}

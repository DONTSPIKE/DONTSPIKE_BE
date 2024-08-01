package org.boot.dontspike.FoodWiki;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.Food.Food;

@Getter
@Setter
@Entity
@Table(name="foodwiki")
public class Foodwiki {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodwikiId;

    @Column(name="fooddata_id")
    private Long fooddataId;

    @OneToOne
    @JoinColumn(name = "fooddata_id", referencedColumnName = "fooddata_id", insertable = false, updatable = false)
    private Food food;

    @Column(name = "expert_opinion", length = 300)
    private String expertOpinion;

    @Column(name = "proper_intake", length = 300)
    private String properIntake;

    @Column(name = "ingestion_method", length = 300)
    private String ingestionMethod;

    @Column(name = "GI")
    private Double gi;
}

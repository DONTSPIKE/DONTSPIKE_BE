package org.boot.dontspike.Food;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Favorites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int favoritesid;

    @Column(name="fooddata_id")
    private int fooddataid;
}

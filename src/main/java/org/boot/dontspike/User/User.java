package org.boot.dontspike.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.BloodSugar.BloodSugar;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @OneToMany(mappedBy = "user")
    private List<BloodSugar> bloodSugars = new ArrayList<>();
}
package org.boot.dontspike.User;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.boot.dontspike.BloodSugar.BloodSugar;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @OneToMany(mappedBy = "user")
    private List<BloodSugar> bloodSugars = new ArrayList<>();

    //==유저 생성자 메서드==//
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {}
}
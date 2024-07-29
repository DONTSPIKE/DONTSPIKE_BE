package org.boot.dontspike.BloodSugar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BloodSugarController {
    @Autowired
    private BloodSugarService bloodSugarService;


//    @PostMapping("/blood-sugar")
//    public String addBloodSugarRecord() {
//
//    }
}

package org.boot.dontspike.BloodSugar;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController//json파일 보낼때 사용하는 annotation
@RequiredArgsConstructor

public class BloodSugarController {

    private final BloodSugarService bloodSugarService;

    @GetMapping("/api/blood-sugar/food/{user_id}")
    public List<GraphDto> getGraph(@PathVariable Long user_id) {
        return bloodSugarService.getGraph(user_id);
    }

}

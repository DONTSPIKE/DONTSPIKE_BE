package org.boot.dontspike.BloodSugar;

import org.boot.dontspike.DTO.BloodSugarInputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BloodSugarService {
    @Autowired
    private BloodSugarRepository bloodSugarRepository;

    public void addBloodSugar(BloodSugarInputDto input, Long userId){
        BloodSugar bloodSugar = new BloodSugar();
        bloodSugar.setUserId(userId);
        bloodSugar.setRecordDate(LocalDateTime.parse(input.getDate(), DateTimeFormatter.ISO_DATE_TIME));
        bloodSugarRepository.save(bloodSugar);
    }
}

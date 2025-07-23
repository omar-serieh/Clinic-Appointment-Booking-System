package com.spring.clinic.dto.patient;

import jakarta.validation.constraints.Null;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class PatientRecordUpdateDTO {
    private String diagnosis;

    private String notes;

    private String gender;

    private Integer age;

    @Null
    private List<MultipartFile> newImages;

    private List<Long> imagesToDelete;
}

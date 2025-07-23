package com.spring.clinic.dto.patient;

import lombok.Data;

@Data
public class PatientRecordRequestDTO {
    private String diagnosis;
    private String notes;
    private String gender;


}

package com.spring.clinic.dto.patient;

import lombok.Data;

@Data
public class PatientProfileDTO {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private int age;

}

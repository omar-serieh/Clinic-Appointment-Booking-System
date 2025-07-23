package com.spring.clinic.dto.doctorDetails;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DoctorDetailsUpdateDTO {
    private String bio;
    private LocalDate startedWorkingAt;
    private String notes;
}

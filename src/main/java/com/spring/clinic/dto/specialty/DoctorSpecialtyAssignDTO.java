package com.spring.clinic.dto.specialty;

import lombok.Data;

import java.util.List;

@Data
public class DoctorSpecialtyAssignDTO {
    private Long doctorId;
    private List<Long> specialtyIds;
}

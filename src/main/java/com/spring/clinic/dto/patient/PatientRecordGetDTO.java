package com.spring.clinic.dto.patient;

import com.spring.clinic.dto.doctorDetails.DoctorProfileDTO;
import com.spring.clinic.entity.RecordImages;
import com.spring.clinic.entity.Users;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatientRecordGetDTO {
    private Long id;
    private String diagnosis;
    private String notes;
    private Integer age;
    private String gender;
    private LocalDateTime createdAt;
    private List<ImageDTO> images;
    private PatientProfileDTO patient;
    private PatientRecordDTO.DoctorProfile doctor;

}

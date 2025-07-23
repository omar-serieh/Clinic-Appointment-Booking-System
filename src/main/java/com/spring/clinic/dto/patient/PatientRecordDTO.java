package com.spring.clinic.dto.patient;

import com.spring.clinic.dto.doctorDetails.DoctorProfileDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PatientRecordDTO {
    private long id;
    private PatientProfileDTO patient;
    private DoctorProfile doctorProfile;
    private String diagnosis;
    private String notes;
    private int age;
    private LocalDateTime createdAt;
    private String gender;
    private List<ImageDTO> images;
    @Data
    public static class DoctorProfile {
        private long id;
        private String name;
        private String email;
        private String phone;
    }
}

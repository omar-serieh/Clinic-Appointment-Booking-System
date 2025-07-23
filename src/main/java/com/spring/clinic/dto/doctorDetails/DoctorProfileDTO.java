package com.spring.clinic.dto.doctorDetails;

import com.spring.clinic.entity.Specialties;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class DoctorProfileDTO {
    private long id;
    private String fullName;
    private String email;
    private String phone;
    private Set<Specialties> specialties = new HashSet<>();
    private String bio;
    private String notes;
    private int yearsOfExperience;
}

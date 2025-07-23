package com.spring.clinic.dto.doctorDetails;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class DoctorRegisterDTO {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    @NotEmpty
    private Set<@NotNull Long> specialtyIds = new HashSet<>();
    private String bio;
    private String notes;
    private LocalDate startedWorkingAt;
    private int age;
}

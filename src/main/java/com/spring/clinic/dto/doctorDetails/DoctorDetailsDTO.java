package com.spring.clinic.dto.doctorDetails;

import com.spring.clinic.entity.Specialties;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class DoctorDetailsDTO {
    private long id;
    private String bio;
    private LocalDate startedWorkingAt;
    private String notes;
    private DoctorInfo doctorInfo;
    @Data
    public static class DoctorInfo{
        private long id;
        private String doctorName;
        private String doctorEmail;
        private String doctorPhone;
        private Set<Specialties> specialties = new HashSet<>();

    }

}

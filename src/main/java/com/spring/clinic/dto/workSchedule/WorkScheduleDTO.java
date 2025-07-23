package com.spring.clinic.dto.workSchedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.clinic.entity.Specialties;
import lombok.Data;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class WorkScheduleDTO {
    private long id;
    private String dayOfWeek;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private int slotDuration;
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

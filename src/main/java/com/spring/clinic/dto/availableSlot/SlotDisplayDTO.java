package com.spring.clinic.dto.availableSlot;

import com.spring.clinic.entity.Specialties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class SlotDisplayDTO {
    private long id;
    private String dayOfWeek;
    private LocalDateTime slotDateTime;
    private boolean isBooked;
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

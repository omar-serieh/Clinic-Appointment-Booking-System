// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.dto.appointment;
import com.spring.clinic.entity.Specialties;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class AppointmentDTO {
    private long id;
    private PatientInfo patientInfo;
    private DoctorInfo doctorInfo;
    private SlotInfo slotInfo;
    private LocalDateTime appointmentTime;
    private String status;
    @Data
    public static class PatientInfo{
        private long id;
        private String patientName;
        private String patientEmail;
        private String patientPhone;
    }
    @Data
    public static class DoctorInfo{
        private long id;
        private String doctorName;
        private String doctorEmail;
        private String doctorPhone;
        private Set<Specialties> specialties = new HashSet<>();

    }
    @Data
    public static class SlotInfo{
        private long id;
        private Boolean isBooked;
        private String dayOfWeek;
        private LocalDateTime slotDateTime;
    }


}

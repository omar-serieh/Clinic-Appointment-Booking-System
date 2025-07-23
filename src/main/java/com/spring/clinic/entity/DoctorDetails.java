package com.spring.clinic.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "doctor_details")
public class DoctorDetails {
    @Id
    private Long doctorId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "doctor_id")
    private Users doctor;

    private String bio;

    private LocalDate startedWorkingAt;

    private String notes;


}

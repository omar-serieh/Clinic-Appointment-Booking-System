package com.spring.clinic.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "available_slots")
public class AvailableSlots {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "doctor_id")
    @ManyToOne
    private Users doctorId;
    @Enumerated(EnumType.STRING)
    private WorkSchedule.DayOfWeek dayOfWeek;

    @Column(name = "slot_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime slotDateTime;

    private Boolean generate;


    private boolean isBooked;

}

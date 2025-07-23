package com.spring.clinic.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;


@Entity
@Data
@Table(name = "work_schedule")
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "doctor_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Users doctorId;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;

    private int slotDuration;

    public enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }



}

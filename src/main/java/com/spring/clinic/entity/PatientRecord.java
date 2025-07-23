package com.spring.clinic.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "patient_record")
public class PatientRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Users patientId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Users doctorId;

    @Column
    private String diagnosis;

    @Column
    private String notes;

    private int age;

    @Column(length = 20)
    private String gender;

    @OneToMany(
            mappedBy = "recordId",
            cascade = CascadeType.ALL
    )
    private List<RecordImages> images = new ArrayList<>();
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
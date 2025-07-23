package com.spring.clinic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "specialties")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    private String name;

    private String password;

    @Email
    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UerRole role; // Changed enum name to follow conventions
    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonIgnore
    private DoctorDetails doctorDetails;
    @ManyToMany(fetch = FetchType.EAGER) // Consider EAGER for this relationship
    @JoinTable(
            name = "doctor_specialties",
            joinColumns = @JoinColumn(name = "doctor_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    )
    private Set<Specialties> specialties = new HashSet<>(); // Initialize collection

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Notifications> notifications;

    private int age;

    private boolean isActive;

    private boolean isVerified;


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public enum UerRole {
        ADMIN,
        DOCTOR,
        PATIENT
    }
}
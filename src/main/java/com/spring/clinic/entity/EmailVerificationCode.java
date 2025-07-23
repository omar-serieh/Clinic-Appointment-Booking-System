package com.spring.clinic.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "email_verification_code")
public class EmailVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String code;
    private LocalDateTime expiry;
    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Users userId;


}

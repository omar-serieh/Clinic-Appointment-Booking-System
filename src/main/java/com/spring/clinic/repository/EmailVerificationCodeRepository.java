package com.spring.clinic.repository;

import com.spring.clinic.entity.EmailVerificationCode;
import com.spring.clinic.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, Long> {
    Optional<EmailVerificationCode> findByUserId(Users user);
}

package com.spring.clinic.service;


import com.spring.clinic.dto.users.UserResponseDTO;
import com.spring.clinic.dto.verificationCode.VerificationDTO;

public interface EmailVerificationCodeService {
    UserResponseDTO verifyCode(VerificationDTO verificationDTO);
    void resendCode(String email);
}

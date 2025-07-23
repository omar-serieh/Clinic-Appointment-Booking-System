package com.spring.clinic.service;

import com.spring.clinic.dto.users.UserResponseDTO;
import com.spring.clinic.dto.verificationCode.VerificationDTO;
import com.spring.clinic.entity.EmailVerificationCode;
import com.spring.clinic.entity.Users;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.EmailVerificationCodeRepository;
import com.spring.clinic.repository.UsersRepository;
import com.spring.clinic.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailVerificationCodeServiceImpl implements EmailVerificationCodeService {
    private final UsersRepository usersRepository;
    private final EmailVerificationCodeRepository codeRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public EmailVerificationCodeServiceImpl(UsersRepository usersRepository, EmailVerificationCodeRepository codeRepository, EmailService emailService, JwtUtil jwtUtil) {
        this.usersRepository = usersRepository;
        this.codeRepository = codeRepository;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    public UserResponseDTO verifyCode(VerificationDTO verificationDTO) {
        Users user = usersRepository.findByEmail(verificationDTO.getEmail())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        EmailVerificationCode verification = codeRepository.findByUserId(user)
                .orElseThrow(() -> new ApiException("Verification code not found",HttpStatus.NOT_FOUND));

        if (verification.getExpiry().isBefore(LocalDateTime.now())) {
            throw new ApiException("Code expired",HttpStatus.REQUEST_TIMEOUT);
        }

        if (!verification.getCode().equals(verificationDTO.getCode())) {
            throw new ApiException("Invalid code",HttpStatus.BAD_REQUEST);
        }
        Users verifiedUser = usersRepository.findByEmail(verificationDTO.getEmail())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        String token = jwtUtil.generateToken(verifiedUser.getEmail(), String.valueOf(verifiedUser.getRole()));
        UserResponseDTO response = new UserResponseDTO();
        response.setUserId(verifiedUser.getUserId());
        response.setUserName(verifiedUser.getName());
        response.setEmail(verifiedUser.getEmail());
        response.setPhoneNumber(verifiedUser.getPhoneNumber());
        response.setToken(token);
        response.setUserRole(verifiedUser.getRole().name());
        response.setVerified(true);
        user.setVerified(true);
        usersRepository.save(user);
        codeRepository.delete(verification);
        return response;
    }

    public void resendCode(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        String code = generateCode();

        EmailVerificationCode verification = codeRepository.findByUserId(user)
                .orElse(new EmailVerificationCode());

        verification.setUserId(user);
        verification.setCode(code);
        verification.setExpiry(LocalDateTime.now().plusMinutes(10));

        codeRepository.save(verification);

        emailService.sendVerificationEmail(user.getEmail(), code);
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}

package com.spring.clinic.controller;

import com.spring.clinic.dto.doctorDetails.DoctorRegisterDTO;
import com.spring.clinic.dto.users.UserLoginDTO;
import com.spring.clinic.dto.users.UserRegisterDTO;
import com.spring.clinic.dto.users.UserResponseDTO;
import com.spring.clinic.dto.verificationCode.VerificationDTO;
import com.spring.clinic.service.EmailVerificationCodeService;
import com.spring.clinic.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UsersService userService;
    private final EmailVerificationCodeService emailVerificationService;

    public AuthController(UsersService userService, EmailVerificationCodeService emailVerificationService) {
        this.userService = userService;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO request) {
        return userService.loginUser(request);
    }


    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@RequestBody UserRegisterDTO request) {
        userService.registerUserAsPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Account created. Please check your email to verify your account.");
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendVerificationCode(@RequestBody String email) {
        emailVerificationService.resendCode(email);
        return ResponseEntity.ok("Verification code resent.");
    }
    @PostMapping("/verify-email")
    public ResponseEntity<UserResponseDTO> verifyEmail(@RequestBody VerificationDTO verificationDTO) {
        UserResponseDTO response= emailVerificationService.verifyCode(verificationDTO);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody DoctorRegisterDTO request) {
        userService.registerUserAsDoctor(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Account created. Please check your email to verify your account.");
    }






}

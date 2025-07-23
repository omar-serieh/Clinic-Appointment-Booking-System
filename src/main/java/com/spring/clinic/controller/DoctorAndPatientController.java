package com.spring.clinic.controller;

import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.doctorDetails.DoctorProfileDTO;
import com.spring.clinic.dto.patient.PatientProfileDTO;
import com.spring.clinic.dto.users.PasswordUpdateDTO;
import com.spring.clinic.dto.users.UserDTO;
import com.spring.clinic.dto.users.UserUpdateDTO;
import com.spring.clinic.entity.DoctorDetails;
import com.spring.clinic.service.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class DoctorAndPatientController {
    private final UsersService userService;
    public DoctorAndPatientController(UsersService userService) {
        this.userService = userService;
    }
    @GetMapping("/public/all-doctors")
    public ResponseEntity<List<DoctorDetailsDTO>> getAllDoctors() {
        List<DoctorDetailsDTO> users=userService.findAllDoctorsDetails();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/doctor")
    public ResponseEntity<DoctorDetails> getDoctorProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DoctorDetails doctorDetails = userService.getDoctorPrivateProfile(userDetails);
        return ResponseEntity.ok(doctorDetails);
    }
    @GetMapping("/public/doctor/{id}")
    public ResponseEntity<DoctorProfileDTO>getDoctorPublicProfile(@PathVariable long id) {
        DoctorProfileDTO user = userService.getDoctorPublicProfile(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/patient")
    public ResponseEntity<UserDTO> getPatientPrivateProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserDTO users = userService.getPatientPrivateProfile(userDetails);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/public/patient/{id}")
    public ResponseEntity<PatientProfileDTO>getPatientPublicProfile(@PathVariable long id) {
        PatientProfileDTO user = userService.getPatientPublicProfile(id);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/public")
    public ResponseEntity<?> UpdateMyProfile(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UserUpdateDTO dto) {
       return userService.updateMyProfile(userDetails,dto);
    }
    @PutMapping("/public/my-password")
    public ResponseEntity<?> updateMyPassword(@AuthenticationPrincipal UserDetails userDetails,@RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return userService.updateMyPassword(userDetails,passwordUpdateDTO);
    }
    @PutMapping("/public/delete")
    public  ResponseEntity<?> deleteMyProfile(@AuthenticationPrincipal UserDetails userDetails){
        return userService.deleteMyProfile(userDetails);
    }
}

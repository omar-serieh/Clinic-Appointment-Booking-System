package com.spring.clinic.service;

import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.doctorDetails.DoctorProfileDTO;
import com.spring.clinic.dto.doctorDetails.DoctorRegisterDTO;
import com.spring.clinic.dto.patient.PatientProfileDTO;
import com.spring.clinic.dto.users.*;
import com.spring.clinic.entity.DoctorDetails;
import com.spring.clinic.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface UsersService {
    ResponseEntity<?> loginUser(UserLoginDTO user);
    List<Users> findAllUsers();
    Users findUserById(long id);
    void registerUserAsPatient(UserRegisterDTO user);
    Users findUserByEmail(String email);
    void registerUserAsDoctor(DoctorRegisterDTO user);
    UserDTO getPatientPrivateProfile(@AuthenticationPrincipal UserDetails user);
    PatientProfileDTO getPatientPublicProfile(long id);
    DoctorProfileDTO getDoctorPublicProfile(long id);
    DoctorDetails getDoctorPrivateProfile(@AuthenticationPrincipal UserDetails user);
    ResponseEntity<?> deleteUserById(long id);
    ResponseEntity<?> deleteMyProfile(@AuthenticationPrincipal UserDetails user);
    ResponseEntity<UserDTO> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails, UserUpdateDTO userUpdateDTO);
    ResponseEntity<?> updateMyPassword(@AuthenticationPrincipal UserDetails userDetails, PasswordUpdateDTO userUpdateDTO);
    Users fetchUserByToken(@AuthenticationPrincipal UserDetails user);
    List<DoctorDetailsDTO> findAllDoctorsDetails();
    void setUserActive(long id);
    List<Users> findAllPatients();
    List<Users> findAllDoctors();
    




}

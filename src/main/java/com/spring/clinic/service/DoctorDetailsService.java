package com.spring.clinic.service;


import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.doctorDetails.DoctorDetailsUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

public interface DoctorDetailsService {
    DoctorDetailsDTO findDoctorDetails(long id);
    DoctorDetailsDTO findMyDoctorDetails(@AuthenticationPrincipal UserDetails userDetails);
    ResponseEntity<DoctorDetailsDTO>updateDoctorDetails(@AuthenticationPrincipal UserDetails userDetails, DoctorDetailsUpdateDTO doctorDetailsUpdateDTO);


}

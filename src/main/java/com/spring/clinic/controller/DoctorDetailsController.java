package com.spring.clinic.controller;

import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.doctorDetails.DoctorDetailsUpdateDTO;
import com.spring.clinic.service.DoctorDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor-details")
public class DoctorDetailsController {
    private final DoctorDetailsService doctorDetailsService;
    public DoctorDetailsController(DoctorDetailsService doctorDetailsService) {
        this.doctorDetailsService = doctorDetailsService;
    }
    @GetMapping("/pub/{id}")
    public ResponseEntity<DoctorDetailsDTO> getDoctorDetails(@PathVariable long id) {
        DoctorDetailsDTO detailsDTO= doctorDetailsService.findDoctorDetails(id);
        return ResponseEntity.ok(detailsDTO);
    }
    @GetMapping("/my")
    public ResponseEntity<DoctorDetailsDTO> getMyDoctorDetails(@AuthenticationPrincipal UserDetails doctorDetails) {
        DoctorDetailsDTO doctorDetailsDTO= doctorDetailsService.findMyDoctorDetails(doctorDetails);
        return ResponseEntity.ok(doctorDetailsDTO);
    }
    @PutMapping("/my")
    public ResponseEntity<DoctorDetailsDTO> updateMyDoctorDetails(@AuthenticationPrincipal UserDetails doctorDetails,@RequestBody DoctorDetailsUpdateDTO doctorDetailsUpdateDTO) {
        return doctorDetailsService.updateDoctorDetails(doctorDetails,doctorDetailsUpdateDTO);
    }

}

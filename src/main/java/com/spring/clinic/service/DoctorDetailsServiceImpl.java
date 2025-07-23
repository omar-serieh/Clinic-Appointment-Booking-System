package com.spring.clinic.service;

import com.spring.clinic.dto.doctorDetails.DoctorDetailsDTO;
import com.spring.clinic.dto.doctorDetails.DoctorDetailsUpdateDTO;
import com.spring.clinic.entity.DoctorDetails;
import com.spring.clinic.entity.Users;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.DoctorDetailsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DoctorDetailsServiceImpl implements DoctorDetailsService{

    private final DoctorDetailsRepository doctorDetailsRepository;
    private final UsersService usersService;
    public DoctorDetailsServiceImpl(DoctorDetailsRepository doctorDetailsRepository,UsersService usersService) {
        this.doctorDetailsRepository = doctorDetailsRepository;
        this.usersService = usersService;
    }
    @Override
    public DoctorDetailsDTO findDoctorDetails(long id) {
        Optional<DoctorDetails> details = doctorDetailsRepository.findById(id);
        if (details.isEmpty()) {
            throw new ApiException("User not found", HttpStatus.NOT_FOUND);
        }
        DoctorDetails doctorDetails = details.get();
        DoctorDetailsDTO detailsDTO = new DoctorDetailsDTO();
        DoctorDetailsDTO.DoctorInfo doctorInfo = new DoctorDetailsDTO.DoctorInfo();
        Users doctor = doctorDetails.getDoctor();
        doctorInfo.setId(doctorDetails.getDoctorId());
        doctorInfo.setDoctorName(doctor.getName());
        doctorInfo.setDoctorEmail(doctor.getEmail());
        doctorInfo.setDoctorPhone(doctor.getPhoneNumber());
        doctorInfo.setSpecialties(doctor.getSpecialties());
        detailsDTO.setId(doctor.getUserId());
        detailsDTO.setNotes(doctorDetails.getNotes());
        detailsDTO.setBio(doctorDetails.getBio());
        detailsDTO.setStartedWorkingAt(doctorDetails.getStartedWorkingAt());
        detailsDTO.setDoctorInfo(doctorInfo);
        return detailsDTO;
    }
    @Override
    public DoctorDetailsDTO findMyDoctorDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Users doctors=usersService.fetchUserByToken(userDetails);
        DoctorDetailsDTO detailsDTO = new DoctorDetailsDTO();
        DoctorDetails doctorDetails= doctorDetailsRepository.findByDoctor(doctors);
        if(doctorDetails==null) {
            throw new ApiException("User Details not found", HttpStatus.NOT_FOUND);
        }
        detailsDTO.setId(doctorDetails.getDoctorId());
        detailsDTO.setNotes(doctorDetails.getNotes());
        detailsDTO.setBio(doctorDetails.getBio());
        detailsDTO.setStartedWorkingAt(doctorDetails.getStartedWorkingAt());
        DoctorDetailsDTO.DoctorInfo doctorInfo = new DoctorDetailsDTO.DoctorInfo();
        doctorInfo.setId(doctors.getUserId());
        doctorInfo.setDoctorName(doctors.getName());
        doctorInfo.setDoctorEmail(doctors.getEmail());
        doctorInfo.setDoctorPhone(doctors.getPhoneNumber());
        doctorInfo.setSpecialties(doctors.getSpecialties());
        detailsDTO.setDoctorInfo(doctorInfo);
        return detailsDTO;
    }

    @Override
    @Transactional
    public ResponseEntity<DoctorDetailsDTO> updateDoctorDetails(@AuthenticationPrincipal UserDetails userDetails, DoctorDetailsUpdateDTO doctorDetailsUpdateDTO) {
        Users doctors=usersService.fetchUserByToken(userDetails);
        DoctorDetails doctorDetails = doctorDetailsRepository.findByDoctor(doctors);
        if(doctorDetails==null) {
            throw new ApiException("User Details not found", HttpStatus.NOT_FOUND);
        }
        if(doctorDetailsUpdateDTO.getNotes()!=null) {
            doctorDetails.setNotes(doctorDetailsUpdateDTO.getNotes());
        }
        if(doctorDetailsUpdateDTO.getBio()!=null) {
            doctorDetails.setBio(doctorDetailsUpdateDTO.getBio());
        }
        if(doctorDetailsUpdateDTO.getStartedWorkingAt()!=null) {
            doctorDetails.setStartedWorkingAt(doctorDetailsUpdateDTO.getStartedWorkingAt());
        }
        doctorDetailsRepository.save(doctorDetails);
        DoctorDetailsDTO detailsDTO = new DoctorDetailsDTO();
        detailsDTO.setId(doctorDetails.getDoctorId());
        detailsDTO.setNotes(doctorDetails.getNotes());
        detailsDTO.setBio(doctorDetails.getBio());
        detailsDTO.setStartedWorkingAt(doctorDetails.getStartedWorkingAt());
        DoctorDetailsDTO.DoctorInfo doctorInfo = new DoctorDetailsDTO.DoctorInfo();
        doctorInfo.setId(doctors.getUserId());
        doctorInfo.setDoctorName(doctors.getName());
        doctorInfo.setDoctorEmail(doctors.getEmail());
        doctorInfo.setDoctorPhone(doctors.getPhoneNumber());
        doctorInfo.setSpecialties(doctors.getSpecialties());
        detailsDTO.setDoctorInfo(doctorInfo);

        return ResponseEntity.ok(detailsDTO);
    }

}

package com.spring.clinic.service;

import com.spring.clinic.dto.patient.PatientRecordDTO;
import com.spring.clinic.dto.patient.PatientRecordGetDTO;
import com.spring.clinic.dto.patient.PatientRecordRequestDTO;
import com.spring.clinic.dto.patient.PatientRecordUpdateDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PatientRecordService {
    PatientRecordDTO createPatientRecord(@AuthenticationPrincipal UserDetails userDetails,
                                         PatientRecordRequestDTO patientRecordRequestDTO,
                                         long patientId, MultipartFile[] imageFiles);
    PatientRecordDTO updatePatientRecord(@AuthenticationPrincipal UserDetails userDetails,
                                         Long recordId,
                                         PatientRecordUpdateDTO updateDTO,
                                         MultipartFile[] newImages);
    void deletePatientRecord(@AuthenticationPrincipal UserDetails userDetails,long id);
    PatientRecordGetDTO getPatientRecordById(@AuthenticationPrincipal UserDetails userDetails, long recordId);
    List<PatientRecordDTO> getMyRecords(@AuthenticationPrincipal UserDetails userDetails);
    List<PatientRecordDTO> getPatientRecords(@AuthenticationPrincipal UserDetails userDetails);
    PatientRecordGetDTO getPatientRecordByIdPat(@AuthenticationPrincipal UserDetails userDetails, long recordId);
    List<PatientRecordDTO> getRecordByPatientId(long patientId);

}

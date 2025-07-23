package com.spring.clinic.controller;

import com.spring.clinic.dto.patient.PatientRecordDTO;
import com.spring.clinic.dto.patient.PatientRecordGetDTO;
import com.spring.clinic.dto.patient.PatientRecordRequestDTO;
import com.spring.clinic.dto.patient.PatientRecordUpdateDTO;
import com.spring.clinic.service.PatientRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/patient-record")
public class PatientRecordController {
    private final PatientRecordService patientRecordService;
    public PatientRecordController(PatientRecordService patientRecordService) {
        this.patientRecordService = patientRecordService;
    }
    @PostMapping(value = "/rec/{patientId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPatientRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long patientId,
            @ModelAttribute PatientRecordRequestDTO requestDTO,
            @RequestParam("images") MultipartFile[] images
    ) {
        try {
            PatientRecordDTO record = patientRecordService.createPatientRecord(
                    userDetails,
                    requestDTO,
                    patientId,
                    images
            );

            return ResponseEntity.ok(record);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating record: " + e.getMessage());
        }
    }

    @GetMapping("/rec/all/{patientId}")
    public ResponseEntity<?> getPatientRecord(@PathVariable Long patientId) {
        List<PatientRecordDTO> patientRecordDTO=patientRecordService.getRecordByPatientId(patientId);
        return ResponseEntity.ok(patientRecordDTO);
    }
    @GetMapping("/rec/{recordId}")
    public ResponseEntity<?> getPatientRecord(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long recordId) {
        PatientRecordGetDTO patientRecordGetDTO = patientRecordService.getPatientRecordById(userDetails, recordId);
        return ResponseEntity.ok(patientRecordGetDTO);

    }
    @GetMapping("/pat/{recordId}")
    public ResponseEntity<?> getPatientRecordById(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long recordId) {
        PatientRecordGetDTO patientRecordGetDTO = patientRecordService.getPatientRecordByIdPat(userDetails, recordId);
        return ResponseEntity.ok(patientRecordGetDTO);

    }

    @PutMapping(value = "/rec/{recordId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updatePatientRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                 @PathVariable Long recordId,
                                                 @ModelAttribute PatientRecordUpdateDTO requestDTO,
                                                 @RequestParam(value = "images",required = false) MultipartFile[] images) {

        try {
            PatientRecordDTO record = patientRecordService.updatePatientRecord(
                    userDetails,
                    recordId,
                    requestDTO,
                    images
            );

            return ResponseEntity.ok(record);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating record: " + e.getMessage());
        }
    }
    @GetMapping("/rec/all-records")
    public ResponseEntity<List<PatientRecordDTO>> getAllPatientRecords(@AuthenticationPrincipal UserDetails userDetails) {
        List<PatientRecordDTO> patientRecords=patientRecordService.getPatientRecords(userDetails);
        return ResponseEntity.ok(patientRecords);
    }
    @GetMapping("/pat/my-record")
    public ResponseEntity<List<PatientRecordDTO>> getMyPatientRecord(@AuthenticationPrincipal UserDetails userDetails) {
        List<PatientRecordDTO> patientRecords= patientRecordService.getMyRecords(userDetails);
        return ResponseEntity.ok(patientRecords);
    }
    @DeleteMapping("/rec/{id}")
    public ResponseEntity<?> deletePatientRecord(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        patientRecordService.deletePatientRecord(userDetails, id);
        return ResponseEntity.ok("Record deleted successfully");
    }

    }



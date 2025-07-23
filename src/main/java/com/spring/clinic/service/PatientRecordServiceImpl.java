package com.spring.clinic.service;
// Developed by Omar Abou Serieh - 2025
import com.spring.clinic.dto.patient.*;
import com.spring.clinic.entity.PatientRecord;
import com.spring.clinic.entity.RecordImages;
import com.spring.clinic.entity.Users;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.PatientRecordRepository;
import com.spring.clinic.repository.RecordImagesRepository;
import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class PatientRecordServiceImpl implements PatientRecordService {
    private final UsersService userService;
    private final PatientRecordRepository patientRecordRepository;
    private final RecordImagesRepository recordImagesRepository;
    private final ImageStorageService imageStorageService;

    public PatientRecordServiceImpl(UsersService userService, PatientRecordRepository patientRecordRepository, RecordImagesRepository recordImagesRepository, ImageStorageService imageStorageService) {
        this.userService = userService;
        this.patientRecordRepository = patientRecordRepository;
        this.recordImagesRepository = recordImagesRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    @Transactional
    public PatientRecordDTO createPatientRecord(@AuthenticationPrincipal UserDetails userDetails,
                                                PatientRecordRequestDTO requestDTO,
                                                long patientId,
                                                MultipartFile[] imageFiles) {

        Users doctor=userService.fetchUserByToken(userDetails);
        Users patient = userService.findUserById(patientId);
        if(patient==null) {
            throw new ApiException("Patient not found", HttpStatus.NOT_FOUND);
        }

        PatientRecord record = new PatientRecord();
        record.setPatientId(patient);
        record.setDoctorId(doctor);
        record.setDiagnosis(requestDTO.getDiagnosis());
        record.setNotes(requestDTO.getNotes());
        record.setGender(requestDTO.getGender());
        record.setAge(patient.getAge());
        record.setCreatedAt(LocalDateTime.now());

        if (imageFiles != null) {
            List<RecordImages> images = Arrays.stream(imageFiles)
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String path = storeImage(file);
                        RecordImages img = new RecordImages();
                        img.setImagePath(path);
                        img.setRecordId(record);
                        return img;
                    })
                    .collect(Collectors.toList());
            record.setImages(images);
        }
        PatientRecord savedRecord = patientRecordRepository.save(record);
        return convertToDTO(savedRecord, patient, doctor);
    }

    @Override
    @Transactional
    public PatientRecordDTO updatePatientRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            Long recordId,
            PatientRecordUpdateDTO updateDTO,
            MultipartFile[] newImages
    ) {
        Users doctor=userService.fetchUserByToken(userDetails);
        PatientRecord record = patientRecordRepository.findById(recordId)
                .orElseThrow(() -> new ApiException("Record not found",HttpStatus.NOT_FOUND));
        if (!record.getDoctorId().equals(doctor)) {
            throw new ApiException("Only the record owner can update it", HttpStatus.FORBIDDEN);
        }

        if (updateDTO.getDiagnosis() != null) {
            record.setDiagnosis(updateDTO.getDiagnosis());
        }
        if (updateDTO.getNotes() != null) {
            record.setNotes(updateDTO.getNotes());
        }
        if (updateDTO.getGender() != null) {
            record.setGender(updateDTO.getGender());
        }
        if (updateDTO.getAge() != null) {
            record.setAge(updateDTO.getAge());
        }
        if (updateDTO.getImagesToDelete() != null) {
            updateDTO.getImagesToDelete().forEach(imageId -> recordImagesRepository.findById(imageId).ifPresent(image -> {
                Path imagePath = Paths.get("./images").toAbsolutePath().resolve(image.getImagePath());
                System.out.println("Trying to delete file: " + imagePath);
                try {
                    boolean deleted = Files.deleteIfExists(imagePath);
                    if (deleted) {
                        System.out.println("File deleted successfully: " + imagePath);
                    } else {
                        System.out.println("File not found: " + imagePath);
                    }
                    recordImagesRepository.deleteByIdAndRecordId(imageId, recordId);
                } catch (IOException e) {
                    System.err.println("Failed to delete file: " + imagePath);
                }
            }));
        }
        if (newImages != null && newImages.length > 0) {
            List<RecordImages> images = Arrays.stream(newImages)
                    .filter(file -> !file.isEmpty())
                    .map(file -> {
                        String path = imageStorageService.store(file);
                        RecordImages image = new RecordImages();
                        image.setImagePath("/images/"+path);
                        image.setRecordId(record);
                        return image;
                    })
                    .toList();
            record.getImages().addAll(images);
        }
        PatientRecord updatedRecord = patientRecordRepository.save(record);
        return convertToDTO(updatedRecord);
    }

    @Override
    @Transactional
    public void deletePatientRecord(@AuthenticationPrincipal UserDetails userDetails,long id) {
        Users doctor=userService.fetchUserByToken(userDetails);
        patientRecordRepository.deleteByDoctorIdAndId(doctor,id);

    }


    @Override
    public PatientRecordGetDTO getPatientRecordById(@AuthenticationPrincipal UserDetails userDetails, long recordId) {
        Users doctor=userService.fetchUserByToken(userDetails);
        PatientRecord record = patientRecordRepository.findByDoctorIdAndId(doctor, recordId);
        if (!record.getDoctorId().equals(doctor)) {
            throw new ApiException("Doctor is not the owner of this record",HttpStatus.FORBIDDEN);
        }
        return convertToGetDTO(record, doctor);
    }
    @Override
    public List<PatientRecordDTO> getMyRecords(@AuthenticationPrincipal UserDetails userDetails) {
        Users patient=userService.fetchUserByToken(userDetails);
        List<PatientRecord> patientRecords=patientRecordRepository.findByPatientId(patient);
        return convertToDTOsPatient(patientRecords,patient);
    }

    @Override
    public List<PatientRecordDTO> getPatientRecords(@AuthenticationPrincipal UserDetails userDetails) {
        Users doctorUser=userService.fetchUserByToken(userDetails);
        List<PatientRecord> patientRecords=patientRecordRepository.findByDoctorId(doctorUser);
        return convertToDTOsDoctor(patientRecords,doctorUser);
    }

    @Override
    public PatientRecordGetDTO getPatientRecordByIdPat(UserDetails userDetails, long recordId) {
        Users patient=userService.fetchUserByToken(userDetails);
        PatientRecord record = patientRecordRepository.findByPatientIdAndId(patient, recordId);
        Users doctor= record.getDoctorId();
        return convertToGetDTO(record, doctor);
    }

    @Override
    public List<PatientRecordDTO> getRecordByPatientId(long patientId) {
        Users patient= userService.findUserById(patientId);
        List<PatientRecord> patientRecords=patientRecordRepository.findByPatientId(patient);
        return convertToDTOsPatient(patientRecords,patient);
    }

    private List<PatientRecordDTO> convertToDTOsDoctor(List<PatientRecord> records,Users doctor) {
        return records.stream().map(record ->{
            Users patient=record.getPatientId();
            return convertToDTO(record,patient,doctor);
        }).collect(Collectors.toList());
    }
    private List<PatientRecordDTO> convertToDTOsPatient(List<PatientRecord> records,Users patient) {
        return records.stream().map(record ->{
            Users doctor=record.getDoctorId();
            return convertToDTO(record,patient,doctor);
        }).collect(Collectors.toList());
    }

    private PatientRecordDTO convertToDTO(PatientRecord record, Users patient, Users doctor) {
        PatientRecordDTO patientRecordDTO = new PatientRecordDTO();
        PatientProfileDTO patientProfile = new PatientProfileDTO();
        patientProfile.setId(patient.getUserId());
        patientProfile.setFullName(patient.getName());
        patientProfile.setEmail(patient.getEmail());
        patientProfile.setPhone(patient.getPhoneNumber());
        patientProfile.setAge(patient.getAge());

        PatientRecordDTO.DoctorProfile doctorProfile=new PatientRecordDTO.DoctorProfile();
        doctorProfile.setId(doctor.getUserId());
        doctorProfile.setName(doctor.getName());
        doctorProfile.setEmail(doctor.getEmail());
        doctorProfile.setPhone(doctor.getPhoneNumber());

        patientRecordDTO.setId(record.getId());
        patientRecordDTO.setPatient(patientProfile);
        patientRecordDTO.setDoctorProfile(doctorProfile);
        patientRecordDTO.setDiagnosis(record.getDiagnosis());
        patientRecordDTO.setNotes(record.getNotes());
        patientRecordDTO.setGender(record.getGender());
        patientRecordDTO.setAge(record.getAge());
        patientRecordDTO.setCreatedAt(record.getCreatedAt());
        patientRecordDTO.setImages(convertToImageDTOs(record.getImages()));
        return patientRecordDTO;
    }
    private PatientRecordGetDTO convertToGetDTO(PatientRecord record, Users doctor) {
        PatientRecordGetDTO dto = new PatientRecordGetDTO();
        dto.setId(record.getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setNotes(record.getNotes());
        dto.setAge(record.getAge());
        dto.setGender(record.getGender());
        dto.setCreatedAt(record.getCreatedAt());
        Hibernate.initialize(record.getImages());
        if (record.getImages() != null) {
            dto.setImages(record.getImages().stream()
                    .map(image -> new ImageDTO(image.getId(), image.getImagePath()))
                    .collect(Collectors.toList()));
        }
        dto.setPatient(convertToPatientProfileDTO(record.getPatientId()));
        dto.setDoctor(convertToDoctorProfileDTO(doctor));
        return dto;
    }
    private String storeImage(MultipartFile file) {
        try {
            String uploadDir = "./images/";
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            return "images/" + fileName;
        } catch (IOException e) {
            throw new ApiException("Failed to store image: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private PatientProfileDTO convertToPatientProfileDTO(Users patient) {
        if (patient == null) {
            return null;
        }

        PatientProfileDTO dto = new PatientProfileDTO();
        dto.setId(patient.getUserId());
        dto.setFullName(patient.getName());
        dto.setEmail(patient.getEmail());
        dto.setPhone(patient.getPhoneNumber());
        dto.setAge(patient.getAge());
        return dto;
    }
    private PatientRecordDTO.DoctorProfile convertToDoctorProfileDTO(Users doctor) {
        if (doctor == null) {
            return null;
        }

        PatientRecordDTO.DoctorProfile dto = new PatientRecordDTO.DoctorProfile();
        dto.setName(doctor.getName());
        dto.setEmail(doctor.getEmail());
        dto.setPhone(doctor.getPhoneNumber());
        dto.setId(doctor.getUserId());
        return dto;
    }
    public PatientRecordDTO convertToDTO(PatientRecord record) {
        if (record == null) {
            throw new ApiException("Record not found",HttpStatus.NOT_FOUND);
        }

        PatientRecordDTO dto = new PatientRecordDTO();
        dto.setId(record.getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setNotes(record.getNotes());
        dto.setAge(record.getAge());
        dto.setGender(record.getGender());
        dto.setCreatedAt(record.getCreatedAt());

        if (record.getImages() != null && !record.getImages().isEmpty()) {
            dto.setImages(convertImagesToDTO(record.getImages()));
        }
        if (record.getPatientId() != null) {
            dto.setPatient(convertToPatientProfileDTO(record.getPatientId()));
        }
        if (record.getDoctorId() != null) {
            dto.setDoctorProfile(convertToDoctorProfileDTO(record.getDoctorId()));
        }
        return dto;
    }
    private List<ImageDTO> convertImagesToDTO(List<RecordImages> images) {
        return images.stream()
                .map(this::convertImageToDTO)
                .collect(Collectors.toList());
    }

    private ImageDTO convertImageToDTO(RecordImages image) {
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setUrl(image.getImagePath());
        return dto;
    }
    private List<ImageDTO> convertToImageDTOs(List<RecordImages> images) {
        List<ImageDTO> imageDTOs = new ArrayList<>();
        for (RecordImages image : images) {
            imageDTOs.add(convertImageToDTO(image));
        }
        return imageDTOs;
    }
}

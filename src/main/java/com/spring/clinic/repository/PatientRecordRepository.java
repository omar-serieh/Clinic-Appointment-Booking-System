package com.spring.clinic.repository;

import com.spring.clinic.entity.PatientRecord;
import com.spring.clinic.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PatientRecordRepository extends JpaRepository<PatientRecord, Long> {
    PatientRecord findByDoctorIdAndId(Users doctorId, Long id);
    List<PatientRecord> findByPatientId(Users patientId);
    PatientRecord findByPatientIdAndId(Users patientId, Long id);
    void deleteByDoctorIdAndId(Users doctor,long id);
    List<PatientRecord> findByDoctorId(Users doctor);
}
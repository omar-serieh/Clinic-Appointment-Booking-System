package com.spring.clinic.repository;

import com.spring.clinic.entity.DoctorDetails;
import com.spring.clinic.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorDetailsRepository extends JpaRepository<DoctorDetails, Long> {
    DoctorDetails findByDoctorId(long doctorId);
    DoctorDetails findByDoctor(Users doctor);
}

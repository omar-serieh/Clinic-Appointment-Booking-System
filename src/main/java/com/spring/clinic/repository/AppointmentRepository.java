// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.repository;
import com.spring.clinic.entity.Appointments;
import com.spring.clinic.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointments,Long> {
    List<Appointments> findByPatientId(Users patientId);
    List<Appointments> findByDoctorId(Users doctorId);
    Appointments findByIdAndPatientId(Long id, Users patientId);
    Optional<Appointments>findByIdAndDoctorId(long appointments, Users doctorId);
}

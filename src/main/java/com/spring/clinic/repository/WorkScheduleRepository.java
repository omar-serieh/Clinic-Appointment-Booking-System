package com.spring.clinic.repository;

import com.spring.clinic.entity.Users;
import com.spring.clinic.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByDoctorId(Users users);
    WorkSchedule findByDoctorIdAndId(Users users, Long id);
}

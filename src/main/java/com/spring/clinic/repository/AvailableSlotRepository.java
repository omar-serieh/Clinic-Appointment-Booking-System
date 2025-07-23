package com.spring.clinic.repository;

import com.spring.clinic.entity.AvailableSlots;
import com.spring.clinic.entity.Users;
import com.spring.clinic.entity.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AvailableSlotRepository extends JpaRepository<AvailableSlots,Long> {
    @Query("SELECT a from AvailableSlots a where a.isBooked=false ")
    List<AvailableSlots> findAvailableSlotsByBookedFalse();
    @Query("SELECT a from AvailableSlots a where a.isBooked=true")
    List<AvailableSlots> findAvailableSlotsByBookedTrue();
    Boolean existsBySlotDateTimeAndDoctorId(LocalDateTime slotDateTime, Users doctorId);
    List<AvailableSlots> findByDoctorId(Users doctorId);
    AvailableSlots findByIdAndDoctorId(Long id, Users doctorId);
    List<AvailableSlots> findByDoctorIdAndDayOfWeekAndSlotDateTimeAfterOrderBySlotDateTimeAsc(Users doctor, WorkSchedule.DayOfWeek dayOfWeek, LocalDateTime slotDateTime);

}

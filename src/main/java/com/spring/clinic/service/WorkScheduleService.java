package com.spring.clinic.service;


import com.spring.clinic.dto.workSchedule.WorkScheduleDTO;
import com.spring.clinic.entity.WorkSchedule;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

public interface WorkScheduleService {
    Optional<WorkSchedule> getWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,long id);
    WorkSchedule addWorkSchedule(@AuthenticationPrincipal UserDetails userDetails, WorkScheduleDTO workSchedule);
    ResponseEntity<?> deleteWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,long id);
    ResponseEntity<?> updateWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,long id, WorkScheduleDTO workSchedule);
    List<WorkScheduleDTO> getDoctorWorkSchedules(long id);
    List<WorkScheduleDTO> getMyWorkSchedules(@AuthenticationPrincipal UserDetails userDetails);
}

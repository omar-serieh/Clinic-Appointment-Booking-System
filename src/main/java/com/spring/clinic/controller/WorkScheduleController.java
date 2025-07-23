package com.spring.clinic.controller;


import com.spring.clinic.dto.workSchedule.WorkScheduleDTO;
import com.spring.clinic.entity.WorkSchedule;
import com.spring.clinic.service.WorkScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/work-schedule")
public class WorkScheduleController {
    private final WorkScheduleService workScheduleService;


    public WorkScheduleController(WorkScheduleService workScheduleService) {
        this.workScheduleService = workScheduleService;
    }

    @PostMapping("/doc")
    public ResponseEntity<WorkSchedule> addWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,@RequestBody WorkScheduleDTO workSchedule) {
        WorkSchedule workSchedule1=workScheduleService.addWorkSchedule(userDetails, workSchedule);
        return ResponseEntity.ok(workSchedule1);
    }
    @GetMapping("/doc/{id}")
    public ResponseEntity<?> getWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,@PathVariable long id) {
        Optional<WorkSchedule> workSchedule= workScheduleService.getWorkSchedule(userDetails,id);
        return ResponseEntity.ok(workSchedule);
    }
    @DeleteMapping("/doc/{id}")
    public ResponseEntity<?> deleteWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,@PathVariable long id) {
        return workScheduleService.deleteWorkSchedule(userDetails,id);
    }
    @PutMapping("/doc/{id}")
    public ResponseEntity<?> updateWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,@PathVariable long id, @RequestBody WorkScheduleDTO workSchedule) {
        return workScheduleService.updateWorkSchedule(userDetails, id, workSchedule);
    }
    @GetMapping("/doc/my")
    public ResponseEntity<?> getMyWorkSchedule(@AuthenticationPrincipal UserDetails userDetails) {
        List<WorkScheduleDTO> workScheduleDTOS=workScheduleService.getMyWorkSchedules(userDetails);
        return ResponseEntity.ok(workScheduleDTOS);
    }
    @GetMapping("/all/{id}")
    public ResponseEntity<?> getAllWorkScheduleByDoctorId(@PathVariable long id) {
        List<WorkScheduleDTO> workScheduleDTO=workScheduleService.getDoctorWorkSchedules(id);
        return ResponseEntity.ok(workScheduleDTO);
    }

}

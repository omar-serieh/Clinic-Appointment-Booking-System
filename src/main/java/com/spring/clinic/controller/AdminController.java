// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.controller;
import com.spring.clinic.dto.appointment.AppointmentDTO;
import com.spring.clinic.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin-appointments")
public class AdminController {
    private final AppointmentService appointmentService;
    public AdminController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    @GetMapping("/{id}")
    ResponseEntity<AppointmentDTO> findAppointmentById(@PathVariable("id") long id) {
        AppointmentDTO appointmentDTO=appointmentService.findAppointmentById(id);
        return ResponseEntity.ok(appointmentDTO);
    }
    @GetMapping("/all")
    ResponseEntity<List<AppointmentDTO>> findAllAppointments() {
        List<AppointmentDTO> appointmentDTOs=appointmentService.findAllAppointments();
        return ResponseEntity.ok(appointmentDTOs);
    }
}

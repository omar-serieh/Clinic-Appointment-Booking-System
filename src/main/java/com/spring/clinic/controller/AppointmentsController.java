package com.spring.clinic.controller;

import com.spring.clinic.dto.appointment.AppointmentDTO;
import com.spring.clinic.dto.appointment.AppointmentUpdateDTO;
import com.spring.clinic.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointment")
public class AppointmentsController {
    private final AppointmentService appointmentService;
    public AppointmentsController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
    @GetMapping("/pat")
    public ResponseEntity<List<AppointmentDTO>> findAppointmentByPatientId(@AuthenticationPrincipal UserDetails userDetails) {
        List<AppointmentDTO> appointmentDTO=appointmentService.findAppointmentsByPatientId(userDetails);
        return ResponseEntity.ok(appointmentDTO);
    }
    @GetMapping("/doc")
    public ResponseEntity<List<AppointmentDTO>> findAppointmentByDoctorId(@AuthenticationPrincipal UserDetails userDetails) {
        List<AppointmentDTO> appointmentDTO=appointmentService.findAppointmentsByDoctorId(userDetails);
        return ResponseEntity.ok(appointmentDTO);
    }

    @PostMapping("/pat/{slotId}")
    public ResponseEntity<AppointmentDTO> bookAppointment(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("slotId") long slotId){
        AppointmentDTO appointmentDTO=appointmentService.bookAppointment(userDetails,slotId);
        return ResponseEntity.ok(appointmentDTO);
    }
    @PutMapping("/pat/cancel/{appointmentId}")
    public ResponseEntity<AppointmentDTO> cancelAppointment(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("appointmentId") long appointmentId) {
        AppointmentDTO appointmentDTO=appointmentService.cancelBookAppointment(userDetails,appointmentId);
        return ResponseEntity.ok(appointmentDTO);
    }
    @PutMapping("/doc/accept/{appointmentId}")
    public ResponseEntity<AppointmentDTO> acceptAppointment(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("appointmentId") long appointmentId) {
        AppointmentDTO appointmentDTO=appointmentService.acceptAppointment(userDetails,appointmentId);
        return ResponseEntity.ok(appointmentDTO);
    }
    @PutMapping("/doc/reject/{appointmentId}")
    public ResponseEntity<AppointmentDTO> rejectAppointment(@AuthenticationPrincipal UserDetails userDetails,@PathVariable("appointmentId") long appointmentId) {
        AppointmentDTO appointmentDTO=appointmentService.rejectAppointment(userDetails,appointmentId);
        return ResponseEntity.ok(appointmentDTO);
    }
    @PutMapping("/doc/update/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AppointmentUpdateDTO appointmentUpdateDTO, @PathVariable("appointmentId") long appointmentId) {
        AppointmentDTO appointmentDTO=appointmentService.updateAppointment(userDetails,appointmentUpdateDTO,appointmentId);
        return ResponseEntity.ok(appointmentDTO);
    }
    @GetMapping("/pub/{appointmentId}")
    public ResponseEntity<AppointmentDTO> findAppointmentById(@PathVariable("appointmentId") long appointmentId) {
        AppointmentDTO appointmentDTO=appointmentService.findAppointmentById(appointmentId);
        return ResponseEntity.ok(appointmentDTO);
    }
    @DeleteMapping("/doc/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable("id") long id) {
        appointmentService.deleteAppointment(userDetails,id);
        return ResponseEntity.ok("Deleted appointment");
    }

}

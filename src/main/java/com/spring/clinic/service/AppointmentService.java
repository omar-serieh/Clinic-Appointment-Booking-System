package com.spring.clinic.service;


import com.spring.clinic.dto.appointment.AppointmentDTO;
import com.spring.clinic.dto.appointment.AppointmentUpdateDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AppointmentService {
    AppointmentDTO findAppointmentById(long id);
    List<AppointmentDTO> findAllAppointments();
    List<AppointmentDTO> findAppointmentsByPatientId(@AuthenticationPrincipal UserDetails patient);
    List<AppointmentDTO> findAppointmentsByDoctorId(@AuthenticationPrincipal UserDetails doctor);
    AppointmentDTO bookAppointment(@AuthenticationPrincipal UserDetails patient , long slotId);
    AppointmentDTO cancelBookAppointment(@AuthenticationPrincipal UserDetails userDetails,long id);
    AppointmentDTO acceptAppointment(@AuthenticationPrincipal UserDetails userDetails, long id);
    AppointmentDTO rejectAppointment(@AuthenticationPrincipal UserDetails userDetails, long id);
    AppointmentDTO updateAppointment(@AuthenticationPrincipal UserDetails userDetails, AppointmentUpdateDTO appointment,long slotId);
    void deleteAppointment(@AuthenticationPrincipal UserDetails userDetails, long id);

}

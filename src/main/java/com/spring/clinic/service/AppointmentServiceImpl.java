package com.spring.clinic.service;
// Developed by Omar Abou Serieh - 2025
import com.spring.clinic.dto.appointment.AppointmentDTO;
import com.spring.clinic.dto.appointment.AppointmentUpdateDTO;
import com.spring.clinic.dto.notifications.NotificationCreateDTO;
import com.spring.clinic.entity.Appointments;
import com.spring.clinic.entity.AvailableSlots;
import com.spring.clinic.entity.Users;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.AppointmentRepository;
import com.spring.clinic.repository.AvailableSlotRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AvailableSlotRepository availableSlotRepository;
    private final UsersService usersService;
    private final NotificationService notificationService;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AvailableSlotRepository availableSlotRepository, UsersService usersService, NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.availableSlotRepository = availableSlotRepository;
        this.usersService = usersService;
        this.notificationService = notificationService;
    }

    @Override
    public AppointmentDTO findAppointmentById(long id) {
        Optional<Appointments> appointments=appointmentRepository.findById(id);
        if(appointments.isEmpty()){
            throw new ApiException("Appointment not found", HttpStatus.NOT_FOUND);
        }
        Appointments appointment=appointments.get();
        return getAppointmentDTO(appointment);
    }

    @Override
    public List<AppointmentDTO> findAllAppointments() {
        List<Appointments> appointments=appointmentRepository.findAll();
        return appointments.stream().map(this::getAppointmentDTO).collect(Collectors.toList());
    }



    @Override
    public List<AppointmentDTO> findAppointmentsByPatientId(@AuthenticationPrincipal UserDetails userDetails) {
        Users patient=usersService.fetchUserByToken(userDetails);
        List<Appointments> appointments= appointmentRepository.findByPatientId(patient);
        if(appointments.isEmpty()){
            throw new ApiException("Appointment not found", HttpStatus.NOT_FOUND);
        }
        return appointments.stream().map(this::getAppointmentDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDTO> findAppointmentsByDoctorId(@AuthenticationPrincipal UserDetails userDetails) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        List<Appointments> appointments= appointmentRepository.findByDoctorId(doctor);
        if(appointments.isEmpty()){
            throw new ApiException("Appointment not found", HttpStatus.NOT_FOUND);
        }
        return appointments.stream().map(this::getAppointmentDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentDTO bookAppointment(@AuthenticationPrincipal UserDetails userDetails,long slotId) {
        Users patient=usersService.fetchUserByToken(userDetails);
        Optional<AvailableSlots> slots=availableSlotRepository.findById(slotId);
        if(slots.isEmpty()){
            throw new ApiException("Slot not found",HttpStatus.NOT_FOUND);
        }
        AvailableSlots slot =slots.get();
        Appointments appointments=new Appointments();
        appointments.setSlotsId(slot);
        appointments.setPatientId(patient);
        appointments.setDoctorId(slot.getDoctorId());
        appointments.setStatus(Appointments.AppointmentStatus.PENDING);
        appointments.setAppointmentDatetime(slot.getSlotDateTime());
        appointmentRepository.save(appointments);
        slot.setBooked(true);
        availableSlotRepository.save(slot);
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Your appointment request has been submitted and is now pending.",
                        "INFO"
                ),patient.getUserId()
        );
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Patient with name " + patient.getName() + "has requested an appointment.",
                        "INFO"
                ),appointments.getDoctorId().getUserId()
        );

        return  getAppointmentDTO(appointments);
    }

    @Override
    @Transactional
    public AppointmentDTO cancelBookAppointment(@AuthenticationPrincipal UserDetails userDetails, long appointmentId) {
        Users patient=usersService.fetchUserByToken(userDetails);
        Appointments appointments=appointmentRepository.findByIdAndPatientId(appointmentId,patient);
        if (appointments == null){
            throw new ApiException("Appointment not found", HttpStatus.NOT_FOUND);
        }
        appointments.setStatus(Appointments.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointments);
        AvailableSlots slot=appointments.getSlotsId();
        slot.setBooked(false);
        availableSlotRepository.save(slot);
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Your appointment request has been Canceled Successfully.",
                        "ALERT"
                ),
                patient.getUserId());
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Patient with name " + patient.getName() + " has Canceled the appointment.",
                        "INFO"
                )
        ,appointments.getDoctorId().getUserId());
        return  getAppointmentDTO(appointments);

    }


    @Override
    @Transactional
    public AppointmentDTO acceptAppointment(@AuthenticationPrincipal UserDetails userDetails,long appointmentId) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        Optional<Appointments> appointments=appointmentRepository.findByIdAndDoctorId(appointmentId,doctor);
        if (appointments.isEmpty()){
            throw new ApiException("Appointment not found", HttpStatus.NOT_FOUND);
        }
        Appointments appointment=appointments.get();
        appointment.setStatus(Appointments.AppointmentStatus.ACCEPTED);
        appointmentRepository.save(appointment);
        AvailableSlots slot=appointment.getSlotsId();
        slot.setBooked(true);
        availableSlotRepository.save(slot);
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Your appointment request has been Accepted.",
                        "CONFIRM"
                ),appointment.getPatientId().getUserId()
        );
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Patient with name " + appointment.getPatientId().getName() + " request for appointment has been Accepted.",
                        "INFO"
                ),doctor.getUserId()
        );
        return  getAppointmentDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentDTO rejectAppointment(@AuthenticationPrincipal UserDetails userDetails,long appointmentId) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        Optional<Appointments> appointments=appointmentRepository.findByIdAndDoctorId(appointmentId,doctor);
        if (appointments.isEmpty()){
            throw new ApiException("Appointment not found", HttpStatus.NOT_FOUND);
        }
        Appointments appointment=appointments.get();
        appointment.setStatus(Appointments.AppointmentStatus.REJECTED);
        appointmentRepository.save(appointment);
        AvailableSlots slot=appointment.getSlotsId();
        slot.setBooked(false);
        availableSlotRepository.save(slot);
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Your appointment request has been Rejected.",
                        "CONFIRM"
                ),appointment.getPatientId().getUserId()
        );
        return  getAppointmentDTO(appointment);
    }

    @Override
    @Transactional
    public AppointmentDTO updateAppointment(@AuthenticationPrincipal UserDetails userDetails, AppointmentUpdateDTO appointmentUpdateDTO,long appointmentId) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        Optional<Appointments> appointment=appointmentRepository.findByIdAndDoctorId(appointmentId,doctor);
        if(appointment.isEmpty()){
            throw new ApiException("Appointment not found",HttpStatus.NOT_FOUND);
        }
        Appointments appointments=appointment.get();
        if(appointmentUpdateDTO.getAppointmentTime() != null){
            appointments.setAppointmentDatetime(appointmentUpdateDTO.getAppointmentTime());
        }
        appointmentRepository.save(appointments);
        notificationService.createNotification(
                new NotificationCreateDTO(
                        "Appointment Request",
                        "Your appointment has been Updated.",
                        "ALERT"
                ),appointments.getPatientId().getUserId()
        );
        return getAppointmentDTO(appointments);
    }

    @Override
    @Transactional
    public void deleteAppointment(UserDetails userDetails, long id) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        Optional<Appointments> appointment=appointmentRepository.findByIdAndDoctorId(id,doctor);
        if (appointment.isEmpty()){
            throw new ApiException("Appointment not found",HttpStatus.NOT_FOUND);
        }
        Appointments appointments=appointment.get();
        appointmentRepository.delete(appointments);
    }

    private AppointmentDTO getAppointmentDTO(Appointments appointment) {
        AppointmentDTO appointmentDTO = new AppointmentDTO();
        AppointmentDTO.DoctorInfo doctorInfo=new AppointmentDTO.DoctorInfo();
        AppointmentDTO.PatientInfo patientInfo=new AppointmentDTO.PatientInfo();
        AppointmentDTO.SlotInfo slotInfo=new AppointmentDTO.SlotInfo();
        Users doctor = appointment.getDoctorId();
        Users patient = appointment.getPatientId();
        AvailableSlots availableSlots=appointment.getSlotsId();
        appointmentDTO.setId(appointment.getId());
        appointmentDTO.setAppointmentTime(availableSlots.getSlotDateTime());
        appointmentDTO.setStatus(String.valueOf(appointment.getStatus()));
        doctorInfo.setId(doctor.getUserId());
        doctorInfo.setDoctorName(doctor.getName());
        doctorInfo.setDoctorEmail(doctor.getEmail());
        doctorInfo.setSpecialties(doctor.getSpecialties());
        doctorInfo.setDoctorPhone(doctor.getPhoneNumber());
        patientInfo.setId(patient.getUserId());
        patientInfo.setPatientName(patient.getName());
        patientInfo.setPatientEmail(patient.getEmail());
        patientInfo.setPatientPhone(patient.getPhoneNumber());
        slotInfo.setId(availableSlots.getId());
        slotInfo.setSlotDateTime(availableSlots.getSlotDateTime());
        slotInfo.setDayOfWeek(String.valueOf(availableSlots.getDayOfWeek()));
        slotInfo.setIsBooked(availableSlots.isBooked());
        appointmentDTO.setDoctorInfo(doctorInfo);
        appointmentDTO.setPatientInfo(patientInfo);
        appointmentDTO.setSlotInfo(slotInfo);
        return appointmentDTO;
    }


}

package com.spring.clinic.service;
// Developed by Omar Abou Serieh - 2025
import com.spring.clinic.dto.workSchedule.WorkScheduleDTO;
import com.spring.clinic.entity.Users;
import com.spring.clinic.entity.WorkSchedule;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.UsersRepository;
import com.spring.clinic.repository.WorkScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WorkScheduleServiceImpl implements WorkScheduleService {
    private final WorkScheduleRepository workScheduleRepository;
    private final UsersService usersService;
    private final AvailableSlotService availableSlotService;
    private final UsersRepository usersRepository;
    public WorkScheduleServiceImpl(WorkScheduleRepository workScheduleRepository, UsersRepository usersRepository, UsersService usersService, AvailableSlotService availableSlotService) {
        this.workScheduleRepository = workScheduleRepository;
        this.usersService = usersService;
        this.availableSlotService = availableSlotService;
        this.usersRepository = usersRepository;
    }


    @Override
    public Optional<WorkSchedule> getWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,long id) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        Optional<WorkSchedule> workSchedule = Optional.ofNullable(workScheduleRepository.findByDoctorIdAndId(doctor, id));
        if(workSchedule.isEmpty()) {
            throw new ApiException("Work schedule not found", HttpStatus.NOT_FOUND);
        }
        return workSchedule;
    }

    @Override
    public WorkSchedule addWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,WorkScheduleDTO workSchedule) {
        Users user=usersService.fetchUserByToken(userDetails);
        WorkSchedule workScheduleEntity = new WorkSchedule();
        workScheduleEntity.setDoctorId(user);
        workScheduleEntity.setDayOfWeek(WorkSchedule.DayOfWeek.valueOf(workSchedule.getDayOfWeek()));
        workScheduleEntity.setStartTime(workSchedule.getStartTime());
        workScheduleEntity.setEndTime(workSchedule.getEndTime());
        workScheduleEntity.setSlotDuration(workSchedule.getSlotDuration());
        return workScheduleRepository.save(workScheduleEntity);

    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,long id) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        Optional<WorkSchedule> workSchedule = Optional.ofNullable(workScheduleRepository.findByDoctorIdAndId(doctor, id));
        if(workSchedule.isEmpty()) {
            throw new ApiException("WorkSchedule not found with id: " + id,HttpStatus.NOT_FOUND);
        }
        workScheduleRepository.deleteById(id);
        return ResponseEntity.ok("Work Schedule Deleted Successfully");
    }

    @Override
    @Transactional
    public ResponseEntity<WorkSchedule> updateWorkSchedule(@AuthenticationPrincipal UserDetails userDetails,long id, WorkScheduleDTO workSchedule) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        WorkSchedule workScheduleEntity = workScheduleRepository.findByDoctorIdAndId(doctor,id);
        if(workScheduleEntity == null) {
            throw new ApiException("You Do Not Have This Work Schedule !",HttpStatus.UNAUTHORIZED);
        }
        if(workSchedule.getDayOfWeek() != null){
            workScheduleEntity.setDayOfWeek(WorkSchedule.DayOfWeek.valueOf(workSchedule.getDayOfWeek()));
        }
        if(workSchedule.getStartTime() != null){
            workScheduleEntity.setStartTime(workSchedule.getStartTime());
        }
        if(workSchedule.getEndTime() != null){
            workScheduleEntity.setEndTime(workSchedule.getEndTime());
        }
        if(workSchedule.getSlotDuration() != 0){
            workScheduleEntity.setSlotDuration(workSchedule.getSlotDuration());
        }
        workScheduleRepository.save(workScheduleEntity);
        availableSlotService.generateAvailableSlots(userDetails);
        return ResponseEntity.ok(workScheduleEntity);
    }

    @Override
    public List<WorkScheduleDTO> getDoctorWorkSchedules(long id) {
        Optional<Users> doctor = usersRepository.findById(id);
        if(doctor.isEmpty()) {
            throw new ApiException("Doctor not found with id: " + id,HttpStatus.NOT_FOUND);
        }
        Users doctorEntity = doctor.get();
        return getWorkScheduleDTOS(doctorEntity);
    }

    @Override
    public List<WorkScheduleDTO> getMyWorkSchedules(@AuthenticationPrincipal UserDetails userDetails) {
        Users doctor=usersService.fetchUserByToken(userDetails);
        return getWorkScheduleDTOS(doctor);
    }



    private List<WorkScheduleDTO> getWorkScheduleDTOS(Users doctor) {
        List<WorkSchedule> workSchedules = workScheduleRepository.findByDoctorId(doctor);
        return workSchedules.stream().map(workSchedule -> {
            WorkScheduleDTO dto = new WorkScheduleDTO();
            dto.setId(workSchedule.getId());
            dto.setStartTime(workSchedule.getStartTime());
            dto.setEndTime(workSchedule.getEndTime());
            dto.setDayOfWeek(String.valueOf(workSchedule.getDayOfWeek()));
            dto.setSlotDuration(workSchedule.getSlotDuration());
            WorkScheduleDTO.DoctorInfo doctorInfo = new WorkScheduleDTO.DoctorInfo();
            doctorInfo.setDoctorName(doctor.getName());
            doctorInfo.setDoctorEmail(doctor.getEmail());
            doctorInfo.setDoctorPhone(doctor.getPhoneNumber());
            doctorInfo.setId(doctor.getUserId());
            doctorInfo.setSpecialties(doctor.getSpecialties());
            dto.setDoctorInfo(doctorInfo);
            return dto;
        }).collect(Collectors.toList());
    }
}

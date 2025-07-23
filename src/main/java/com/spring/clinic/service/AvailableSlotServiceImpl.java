package com.spring.clinic.service;

import com.spring.clinic.dto.availableSlot.SlotCreationDTO;
import com.spring.clinic.dto.availableSlot.SlotDisplayDTO;
import com.spring.clinic.entity.AvailableSlots;
import com.spring.clinic.entity.Users;
import com.spring.clinic.entity.WorkSchedule;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.AvailableSlotRepository;
import com.spring.clinic.repository.WorkScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AvailableSlotServiceImpl implements AvailableSlotService{
    private final WorkScheduleRepository workScheduleRepository;

    private final AvailableSlotRepository availableSlotsRepository;

    private final UsersService usersService;

    public AvailableSlotServiceImpl(WorkScheduleRepository workScheduleRepository, AvailableSlotRepository availableSlotsRepository, UsersService usersService) {
        this.workScheduleRepository = workScheduleRepository;
        this.availableSlotsRepository = availableSlotsRepository;
        this.usersService = usersService;
    }

    @Override
    public List<SlotDisplayDTO> getAvailableSlots() {
        List<AvailableSlots> availableSlots = availableSlotsRepository.findAvailableSlotsByBookedFalse();
        if (availableSlots.isEmpty()) {
            throw new ApiException("No Slots to display", HttpStatus.NOT_FOUND);
        }
        return getSlotDisplayDTOS(availableSlots);
    }
    @Override
    public List<SlotDisplayDTO> getBookedSlots() {
        List<AvailableSlots> availableSlots = availableSlotsRepository.findAvailableSlotsByBookedTrue();
        if (availableSlots.isEmpty()) {
            throw new ApiException("No Slots to display", HttpStatus.NOT_FOUND);
        }
        return getSlotDisplayDTOS(availableSlots);
    }



    public SlotDisplayDTO CreateAvailableSlots(@AuthenticationPrincipal UserDetails userDetails, SlotCreationDTO availableSlots) {
        Users user=usersService.fetchUserByToken(userDetails);
        LocalDateTime slotDateTime = availableSlots.getSlotDateTime();
        boolean isConflict = availableSlotsRepository.existsBySlotDateTimeAndDoctorId(slotDateTime, user);
        if (isConflict) {
            throw new ApiException("The selected slot conflicts with an existing slot.", HttpStatus.CONFLICT);
        }
        try {
            AvailableSlots availableSlot = new AvailableSlots();
            availableSlot.setSlotDateTime(slotDateTime);
            availableSlot.setDoctorId(user);
            availableSlot.setDayOfWeek(WorkSchedule.DayOfWeek.valueOf(availableSlots.getDayOfWeek()));
            availableSlot.setBooked(false);
            availableSlot.setGenerate(false);
            availableSlotsRepository.save(availableSlot);
            SlotDisplayDTO slotDTO = new SlotDisplayDTO();
            slotDTO.setId(availableSlot.getId());
            slotDTO.setDayOfWeek(availableSlots.getDayOfWeek());
            slotDTO.setSlotDateTime(slotDateTime);
            slotDTO.setBooked(false);
            SlotDisplayDTO.DoctorInfo doctorInfo = new SlotDisplayDTO.DoctorInfo();
            doctorInfo.setId(user.getUserId());
            doctorInfo.setDoctorName(user.getName());
            doctorInfo.setDoctorEmail(user.getEmail());
            doctorInfo.setDoctorPhone(user.getPhoneNumber());
            doctorInfo.setSpecialties(user.getSpecialties());
            slotDTO.setDoctorInfo(doctorInfo);
            return slotDTO;
        }catch (Exception e) {
            throw new ApiException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }



    @Override
    @Transactional
    public List<SlotDisplayDTO> generateAvailableSlots(@AuthenticationPrincipal UserDetails userDetails) {
        Users user=usersService.fetchUserByToken(userDetails);
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        List<WorkSchedule> workSchedules = workScheduleRepository.findByDoctorId(user);
        List<AvailableSlots> existingSlots = availableSlotsRepository.findByDoctorId(user);
        for (AvailableSlots slot : existingSlots) {
            if (!slot.isBooked() && slot.getGenerate()) {
                availableSlotsRepository.delete(slot);
            }

        }
        List<SlotDisplayDTO> slotDisplayDTOList = new ArrayList<>();
        for (WorkSchedule schedule : workSchedules) {
            String dayOfWeek = String.valueOf(schedule.getDayOfWeek());
            LocalTime startTime = schedule.getStartTime();
            LocalTime endTime = schedule.getEndTime();
            LocalDate date = startOfWeek.with(DayOfWeek.valueOf(dayOfWeek.toUpperCase()));

            while (startTime.isBefore(endTime)) {
                LocalDateTime dateTime = date.atTime(startTime);

                boolean isSlotExists = availableSlotsRepository.existsBySlotDateTimeAndDoctorId(dateTime, user);
                if (!isSlotExists) {
                    AvailableSlots slot = new AvailableSlots();
                    slot.setSlotDateTime(dateTime);
                    slot.setDoctorId(user);
                    slot.setDayOfWeek(schedule.getDayOfWeek());
                    slot.setBooked(false);
                    slot.setGenerate(true);
                    availableSlotsRepository.save(slot);
                    SlotDisplayDTO slotDTO = new SlotDisplayDTO();
                    slotDTO.setId(slot.getId());
                    slotDTO.setDayOfWeek(schedule.getDayOfWeek().toString());
                    slotDTO.setSlotDateTime(dateTime);
                    slotDTO.setBooked(false);
                    slotDisplayDTOList.add(slotDTO);
                    SlotDisplayDTO.DoctorInfo doctorInfo = new SlotDisplayDTO.DoctorInfo();
                    Users doctor = schedule.getDoctorId();
                    doctorInfo.setId(doctor.getUserId());
                    doctorInfo.setDoctorName(doctor.getName());
                    doctorInfo.setDoctorEmail(doctor.getEmail());
                    doctorInfo.setDoctorPhone(doctor.getPhoneNumber());
                    doctorInfo.setSpecialties(doctor.getSpecialties());
                    slotDTO.setDoctorInfo(doctorInfo);
                }
                startTime = startTime.plusMinutes(schedule.getSlotDuration());
            }
        }
        return slotDisplayDTOList;
    }

    @Override
    @Transactional
    public SlotDisplayDTO updateAvailableSlots(UserDetails userDetails, SlotCreationDTO availableSlots, long id) {
        Users user = usersService.fetchUserByToken(userDetails);
        AvailableSlots slotToUpdate = availableSlotsRepository.findByIdAndDoctorId(id, user);

        if (slotToUpdate == null) {
            throw new ApiException("The selected slot does not exist.", HttpStatus.NOT_FOUND);
        }

        LocalDateTime oldDateTime = slotToUpdate.getSlotDateTime();
        LocalDateTime newDateTime = availableSlots.getSlotDateTime() != null ? availableSlots.getSlotDateTime() : oldDateTime;

        // تعديل اليوم اذا موجود
        if (availableSlots.getDayOfWeek() != null) {
            slotToUpdate.setDayOfWeek(WorkSchedule.DayOfWeek.valueOf(availableSlots.getDayOfWeek()));
        }

        // تعديل وقت السلوط
        slotToUpdate.setSlotDateTime(newDateTime);
        slotToUpdate.setBooked(slotToUpdate.isBooked());
        slotToUpdate.setGenerate(false);
        availableSlotsRepository.save(slotToUpdate);

        // حساب الفرق بين الوقت الجديد والقديم (يمكن يكون موجب أو سالب)
        Duration diff = Duration.between(oldDateTime, newDateTime);

        if (!diff.isZero()) {
            // جلب باقي السلوطات بنفس اليوم والي بعد الوقت القديم
            List<AvailableSlots> slotsToShift = availableSlotsRepository.findByDoctorIdAndDayOfWeekAndSlotDateTimeAfterOrderBySlotDateTimeAsc(
                    user,
                    slotToUpdate.getDayOfWeek(),
                    oldDateTime
            );

            for (AvailableSlots slot : slotsToShift) {
                if (slot.getId() != id) { // ما نزح السلوط يلي عدلناه لأنه بالفعل حدثناه فوق
                    slot.setSlotDateTime(slot.getSlotDateTime().plus(diff));
                    slot.setGenerate(true); // يعتبر مولد تلقائي
                    availableSlotsRepository.save(slot);
                }
            }
        }

        // بناء الDTO
        SlotDisplayDTO slotDTO = new SlotDisplayDTO();
        slotDTO.setId(slotToUpdate.getId());
        slotDTO.setDayOfWeek(slotToUpdate.getDayOfWeek().toString());
        slotDTO.setSlotDateTime(slotToUpdate.getSlotDateTime());
        slotDTO.setBooked(slotToUpdate.isBooked());

        SlotDisplayDTO.DoctorInfo doctorInfo = new SlotDisplayDTO.DoctorInfo();
        doctorInfo.setId(user.getUserId());
        doctorInfo.setDoctorName(user.getName());
        doctorInfo.setDoctorEmail(user.getEmail());
        doctorInfo.setDoctorPhone(user.getPhoneNumber());
        doctorInfo.setSpecialties(user.getSpecialties());
        slotDTO.setDoctorInfo(doctorInfo);

        return slotDTO;
    }

    @Override
    @Transactional
    public void DeleteAvailableSlots(@AuthenticationPrincipal UserDetails userDetails,long id) {
        Users doctor = usersService.fetchUserByToken(userDetails);
        AvailableSlots availableSlots=availableSlotsRepository.findByIdAndDoctorId(id,doctor);
        if (availableSlots== null) {
            throw new ApiException("The selected slot does not exist.", HttpStatus.NOT_FOUND);
        }availableSlotsRepository.delete(availableSlots);
    }


    private List<SlotDisplayDTO> getSlotDisplayDTOS(List<AvailableSlots> availableSlots) {
        return availableSlots.stream().map(availableSlot ->{
            SlotDisplayDTO slotDTO = new SlotDisplayDTO();
            slotDTO.setId(availableSlot.getId());
            slotDTO.setDayOfWeek(String.valueOf(availableSlot.getDayOfWeek()));
            slotDTO.setSlotDateTime(availableSlot.getSlotDateTime());
            slotDTO.setBooked(availableSlot.isBooked());
            SlotDisplayDTO.DoctorInfo doctorInfo = new SlotDisplayDTO.DoctorInfo();
            Users doctor=availableSlot.getDoctorId();
            doctorInfo.setDoctorName(doctor.getName());
            doctorInfo.setId(doctor.getUserId());
            doctorInfo.setDoctorEmail(doctor.getEmail());
            doctorInfo.setDoctorPhone(doctor.getPhoneNumber());
            doctorInfo.setSpecialties(doctor.getSpecialties());
            slotDTO.setDoctorInfo(doctorInfo);
            return slotDTO;
        }).collect(Collectors.toList());
    }
}


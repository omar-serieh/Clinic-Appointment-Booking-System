package com.spring.clinic.controller;

import com.spring.clinic.dto.availableSlot.SlotCreationDTO;
import com.spring.clinic.dto.availableSlot.SlotDisplayDTO;
import com.spring.clinic.entity.AvailableSlots;
import com.spring.clinic.service.AvailableSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/available-slots")
public class AvailableSlotsController {
    private final AvailableSlotService availableSlotService;

    public AvailableSlotsController(AvailableSlotService availableSlotService) {
        this.availableSlotService = availableSlotService;
    }

    @GetMapping("/slot/available")
    public ResponseEntity<List<SlotDisplayDTO>> getAvailableSlots() {
        List<SlotDisplayDTO> availableSlots = availableSlotService.getAvailableSlots();
        return ResponseEntity.ok(availableSlots);
    }
    @GetMapping("/slot/booked")
    public ResponseEntity<List<SlotDisplayDTO>> getBookedSlots() {
        List<SlotDisplayDTO> BookedSlots = availableSlotService.getBookedSlots();
        return ResponseEntity.ok(BookedSlots);
    }
    @PostMapping("/doc/generate")
    ResponseEntity<List<SlotDisplayDTO>> generateSlots(@AuthenticationPrincipal UserDetails userDetails) {
        List<SlotDisplayDTO> availableSlots = availableSlotService.generateAvailableSlots(userDetails);
        return ResponseEntity.ok(availableSlots);

    }
    @PostMapping("/doc/create")
    ResponseEntity<SlotDisplayDTO> createSlot(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SlotCreationDTO slotDTO) {
        SlotDisplayDTO slot=availableSlotService.CreateAvailableSlots(userDetails, slotDTO);
        return ResponseEntity.ok(slot);
    }
    @PutMapping("/doc/update/{id}")
    ResponseEntity<SlotDisplayDTO> updateSlot(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SlotCreationDTO slotDTO,@PathVariable long id) {
        SlotDisplayDTO slot = availableSlotService.updateAvailableSlots(userDetails, slotDTO, id);
        return ResponseEntity.ok(slot);
    }
    @DeleteMapping("/doc/delete/{id}")
    ResponseEntity<?> deleteSlot(@AuthenticationPrincipal UserDetails userDetails,@PathVariable long id) {
        availableSlotService.DeleteAvailableSlots(userDetails,id);
        return ResponseEntity.ok("Deleted Successfully");
    }


}

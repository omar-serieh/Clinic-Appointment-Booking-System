package com.spring.clinic.service;


import com.spring.clinic.dto.availableSlot.SlotCreationDTO;
import com.spring.clinic.dto.availableSlot.SlotDisplayDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface AvailableSlotService {
    List<SlotDisplayDTO> getAvailableSlots();
    List<SlotDisplayDTO> getBookedSlots();
    SlotDisplayDTO CreateAvailableSlots(@AuthenticationPrincipal UserDetails userDetails,SlotCreationDTO availableSlots);
    List<SlotDisplayDTO> generateAvailableSlots(@AuthenticationPrincipal UserDetails userDetails);
    SlotDisplayDTO updateAvailableSlots(@AuthenticationPrincipal UserDetails userDetails,SlotCreationDTO availableSlots,long id);
    void DeleteAvailableSlots(@AuthenticationPrincipal UserDetails userDetails,long id);

}

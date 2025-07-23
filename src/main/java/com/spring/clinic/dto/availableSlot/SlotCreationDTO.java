package com.spring.clinic.dto.availableSlot;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SlotCreationDTO {
    private String dayOfWeek;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime slotDateTime;
    private int slotDuration;
}

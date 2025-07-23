package com.spring.clinic.dto.specialty;

import com.spring.clinic.entity.Specialties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
@Data
public class SpecialtiesDTO {
    @NotEmpty
    private Set<@NotNull Long> specialtyIds = new HashSet<>();
}

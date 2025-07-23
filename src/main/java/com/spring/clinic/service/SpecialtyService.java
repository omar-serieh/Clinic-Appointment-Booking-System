package com.spring.clinic.service;


import com.spring.clinic.dto.specialty.SpecialtiesDTO;
import com.spring.clinic.dto.specialty.SpecialtyDTO;
import com.spring.clinic.entity.Specialties;

import java.util.Set;

public interface SpecialtyService {
    Set<Specialties> getSpecialtiesById(SpecialtiesDTO specialtyDTO);
    Set<Specialties> getAllSpecialties();
    SpecialtyDTO addSpecialty(SpecialtyDTO specialtyDTO);
    SpecialtyDTO updateSpecialty(SpecialtyDTO specialtyDTO,long id);
    void deleteSpecialty(long id);
    SpecialtyDTO getSpecialtyById(long id);
}

package com.spring.clinic.service;

import com.spring.clinic.dto.specialty.SpecialtiesDTO;
import com.spring.clinic.dto.specialty.SpecialtyDTO;
import com.spring.clinic.entity.Specialties;
import com.spring.clinic.exception.ApiException;
import com.spring.clinic.repository.SpecialtyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SpecialtyServiceImpl implements SpecialtyService {
    private final SpecialtyRepository specialtyRepository;

    public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository) {
        this.specialtyRepository = specialtyRepository;
    }

    @Override
    public Set<Specialties> getSpecialtiesById(SpecialtiesDTO specialtyDTO) {
        return fetchAndValidateSpecialties(specialtyDTO.getSpecialtyIds());
    }

    @Override
    public Set<Specialties> getAllSpecialties() {
        Set<Specialties> specialties = specialtyRepository.findAllSpecialties();
        if (specialties.isEmpty()) {
            throw new ApiException("No Specialties found", HttpStatus.NOT_FOUND);
        }
        return specialties;
    }

    @Override
    @Transactional
    public SpecialtyDTO addSpecialty(SpecialtyDTO specialtyDTO) {
        Specialties specialties = new Specialties();
        specialties.setName(specialtyDTO.getName());
        specialtyRepository.save(specialties);
        SpecialtyDTO specialtyDTO1 = new SpecialtyDTO();
        specialtyDTO1.setName(specialtyDTO.getName());
        specialtyDTO1.setId(specialties.getId());
        return specialtyDTO1;
    }

    @Override
    @Transactional
    public SpecialtyDTO updateSpecialty(SpecialtyDTO specialtyDTO,long id) {
        Specialties specialties=specialtyRepository.findSpecialtiesById(id);
        if (specialties == null) {
            throw new ApiException("Specialty not found", HttpStatus.NOT_FOUND);
        }
        specialties.setName(specialtyDTO.getName());
        specialtyRepository.save(specialties);
        SpecialtyDTO specialtyDTO1 = new SpecialtyDTO();
        specialtyDTO1.setName(specialtyDTO.getName());
        specialtyDTO1.setId(specialties.getId());
        return specialtyDTO1;
    }

    @Override
    @Transactional
    public void deleteSpecialty(long id) {
        specialtyRepository.deleteById(id);
    }

    @Override
    public SpecialtyDTO getSpecialtyById(long id) {
        Specialties specialties=specialtyRepository.findSpecialtiesById(id);
        if (specialties == null) {
            throw new ApiException("Specialty not found", HttpStatus.NOT_FOUND);
        }
        SpecialtyDTO specialtyDTO1 = new SpecialtyDTO();
        specialtyDTO1.setName(specialties.getName());
        specialtyDTO1.setId(specialties.getId());
        return specialtyDTO1;
    }
    private Set<Specialties> fetchAndValidateSpecialties(Set<Long> specialtyIds) {
        return getSpecialties(specialtyIds, specialtyRepository);
    }

    static Set<Specialties> getSpecialties(Set<Long> specialtyIds, SpecialtyRepository specialtyRepository) {
        Set<Long> requestedIds = new HashSet<>(specialtyIds);
        Set<Specialties> specialties = new HashSet<>(
                specialtyRepository.findAllById(requestedIds)
        );

        if (specialties.size() != requestedIds.size()) {
            Set<Long> foundIds = specialties.stream()
                    .map(Specialties::getId)
                    .collect(Collectors.toSet());
            Set<Long> missingIds = requestedIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toSet());
            throw new ApiException("Invalid specialty IDs: " + missingIds, HttpStatus.BAD_REQUEST);
        }

        return specialties;
    }
}

package com.spring.clinic.controller;

import com.spring.clinic.dto.specialty.SpecialtiesDTO;
import com.spring.clinic.dto.specialty.SpecialtyDTO;
import com.spring.clinic.entity.Specialties;
import com.spring.clinic.service.SpecialtyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/specialty")
public class SpecialtiesController {
    private final SpecialtyService specialtyService;
    public SpecialtiesController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }
    @GetMapping("{id}")
    public ResponseEntity<SpecialtyDTO> getSpecialty(@PathVariable int id) {
        SpecialtyDTO specialtyDTO=specialtyService.getSpecialtyById(id);
        return ResponseEntity.ok(specialtyDTO);
    }
    @GetMapping("/ids")
    public ResponseEntity<Set<Specialties>> getSpecialties(@RequestBody SpecialtiesDTO specialtyDTO) {
        Set<Specialties> specialties = specialtyService.getSpecialtiesById(specialtyDTO);
        return ResponseEntity.ok(specialties);
    }
    @GetMapping("/all")
    public ResponseEntity<Set<Specialties>> getAllSpecialties() {
        Set<Specialties> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }
}

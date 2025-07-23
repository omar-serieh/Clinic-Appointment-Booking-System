// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.controller;
import com.spring.clinic.dto.specialty.SpecialtiesDTO;
import com.spring.clinic.dto.specialty.SpecialtyDTO;
import com.spring.clinic.entity.Specialties;
import com.spring.clinic.service.SpecialtyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/admin-specialty")
public class AdminSpecialtiesController {
    private final SpecialtyService specialtyService;

    public AdminSpecialtiesController(SpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }
    @GetMapping("/{id}")
    public ResponseEntity<SpecialtyDTO> getSpecialty(@PathVariable int id) {
        SpecialtyDTO specialtyDTO=specialtyService.getSpecialtyById(id);
        return ResponseEntity.ok(specialtyDTO);
    }
    @GetMapping("/ids")
    public ResponseEntity<Set<Specialties>> getSpecialties(@RequestBody SpecialtiesDTO specialtiesDTO) {
        Set<Specialties> specialties = specialtyService.getSpecialtiesById(specialtiesDTO);
        return ResponseEntity.ok(specialties);
    }
    @GetMapping("/all")
    public ResponseEntity<Set<Specialties>> getAllSpecialties() {
        Set<Specialties> specialties = specialtyService.getAllSpecialties();
        return ResponseEntity.ok(specialties);
    }
    @PostMapping("/add")
    public ResponseEntity<SpecialtyDTO> addSpecialties(@RequestBody SpecialtyDTO specialties) {
        SpecialtyDTO specialtyDTO = specialtyService.addSpecialty(specialties);
        return ResponseEntity.ok(specialtyDTO);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<SpecialtyDTO> updateSpecialties(@RequestBody SpecialtyDTO specialties, @PathVariable long id) {
        SpecialtyDTO specialtyDTO=specialtyService.updateSpecialty(specialties,id);
        return ResponseEntity.ok(specialtyDTO);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSpecialties(@PathVariable long id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.ok("Deleted Specialty Successfully");
    }
}

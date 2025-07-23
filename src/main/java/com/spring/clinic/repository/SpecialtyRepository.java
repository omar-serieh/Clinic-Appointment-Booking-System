// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.repository;
import com.spring.clinic.entity.Specialties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Set;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialties,Long> {
    @Query("SELECT s FROM Specialties s")
    Set<Specialties> findAllSpecialties();
    Specialties findSpecialtiesById(long id);

}

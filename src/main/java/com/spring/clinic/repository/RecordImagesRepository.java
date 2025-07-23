// Developed by Omar Abou Serieh - 2025
package com.spring.clinic.repository;
import com.spring.clinic.entity.RecordImages;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface RecordImagesRepository extends CrudRepository<RecordImages, Long> {
    @Modifying
    @Query("DELETE FROM RecordImages ri WHERE ri.id = :imageId AND ri.recordId.id = :recordId")
    void deleteByIdAndRecordId(@Param("imageId") Long imageId, @Param("recordId") Long recordId);}

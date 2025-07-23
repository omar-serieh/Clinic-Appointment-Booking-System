package com.spring.clinic.dto.patient;

import lombok.Data;

@Data
public class ImageDTO {
    private Long id;
    private String url;

    public ImageDTO(Long id, String path) {
        this.id = id;
        this.url = "/images/" + path; // أو المسار الكامل
    }

    public ImageDTO() {

    }
}

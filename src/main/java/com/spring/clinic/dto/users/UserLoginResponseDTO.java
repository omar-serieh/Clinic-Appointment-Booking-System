package com.spring.clinic.dto.users;

import lombok.Data;

@Data
public class UserLoginResponseDTO {
    private String userName;
    private String email;
    private String token;
    private String userRole;
}

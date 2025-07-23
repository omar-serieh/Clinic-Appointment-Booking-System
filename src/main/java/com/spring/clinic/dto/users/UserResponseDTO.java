package com.spring.clinic.dto.users;

import lombok.Data;

@Data
public class UserResponseDTO {
    private long userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private int age;
    private String token;
    private String userRole;
    private boolean isVerified;


}
